package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.BookingDTO;
import com.nestigo.systemdesign.nestigo.dtos.BookingRequestDTO;
import com.nestigo.systemdesign.nestigo.dtos.GuestDTO;
import com.nestigo.systemdesign.nestigo.entities.*;
import com.nestigo.systemdesign.nestigo.entities.enums.BookingStatus;
import com.nestigo.systemdesign.nestigo.exceptions.ResourceNotFoundException;
import com.nestigo.systemdesign.nestigo.exceptions.UnauthorizedException;
import com.nestigo.systemdesign.nestigo.repositories.*;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDTO initializeBooking(BookingRequestDTO bookingRequestDTO) {

        log.info("initializeBooking booking for hotel: {}, room: {}. date {}-{}", bookingRequestDTO.getHotelId(),
                bookingRequestDTO.getRoomId(), bookingRequestDTO.getCheckInDate(), bookingRequestDTO.getCheckOutDate());

        HotelEntity hotel = hotelRepository.findById(bookingRequestDTO.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Hotel NOT found with id:"+ bookingRequestDTO.getHotelId()));

        RoomEntity room = roomRepository.findById(bookingRequestDTO.getRoomId()).orElseThrow(() ->
                new ResourceNotFoundException("Room NOT found with id:"+ bookingRequestDTO.getRoomId()));

        List<InventoryEntity> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequestDTO.getCheckInDate(), bookingRequestDTO.getCheckOutDate(),bookingRequestDTO.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequestDTO.getCheckInDate(), bookingRequestDTO.getCheckOutDate())+1;
        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available");
        }

//      reserving the room
         for(InventoryEntity inventory : inventoryList) {
             inventory.setReservedCount(inventory.getReservedCount()+ bookingRequestDTO.getRoomsCount());
         }

         inventoryRepository.saveAll(inventoryList);



        //TODO: calculate the dynamic price

         //create a booking
        BookingEntity booking =BookingEntity.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequestDTO.getCheckInDate())
                .checkOutDate(bookingRequestDTO.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequestDTO.getRoomsCount())
                .price(BigDecimal.TEN)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);




    }

    @Override
    @Transactional
    public BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDtoList) {
        log.info("Adding guests for booking with id: {}", bookingId);

        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking NOT found with id:"+ bookingId));

        UserEntity user = getCurrentUser();
        System.out.println("booking.userId=" + booking.getUser().getId());
        System.out.println("current.userId=" + getCurrentUser().getId());


        if(!user.equals(booking.getUser())) {
            throw new UnauthorizedException("Booking does not belong to this user " +user.getId());

        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has expired");

        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state");
        }

        for (GuestDTO guestDTO : guestDtoList) {
            GuestEntity guest = modelMapper.map(guestDTO, GuestEntity.class);
            guest.setUser(user);
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);

        }
        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDTO.class);

    }




    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        UserEntity user = getCurrentUser();
        if(!user.equals(booking.getUser())) {
            throw new UnauthorizedException("Booking does not belong to this user" +user.getId());
        }
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking,
                frontendUrl+"/payments/" +bookingId +"/status",
                frontendUrl+"/payments/" +bookingId +"/status");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayments(Event event) {
        if("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session == null) {
                try {
                    session = ApiResource.GSON.fromJson(
                            event.getData().getObject().toJson(),
                            Session.class
                    );
                } catch (Exception e) {
                    log.warn("Failed to deserialize Stripe Session. eventId={}", event.getId(), e);
                    return;
                }
            }

            String sessionId = session.getId();
            BookingEntity booking = bookingRepository.findByPaymentSessionId(sessionId).
                    orElseThrow(()-> new ResourceNotFoundException("Booking not found for session ID:" + sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomsCount());

            log.info("capture payments for booking with id: {}", booking.getId());


        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }



    }

    @Override
    public void cancelBooking(Long bookingId) {

    }

    public boolean hasBookingExpired(BookingEntity bookingDTO) {
        return bookingDTO.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public UserEntity getCurrentUser() {

        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
