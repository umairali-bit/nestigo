package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.BookingDTO;
import com.nestigo.systemdesign.nestigo.dtos.BookingRequestDTO;
import com.nestigo.systemdesign.nestigo.dtos.GuestDTO;
import com.nestigo.systemdesign.nestigo.entities.*;
import com.nestigo.systemdesign.nestigo.entities.enums.BookingStatus;
import com.nestigo.systemdesign.nestigo.exceptions.ResourceNotFoundException;
import com.nestigo.systemdesign.nestigo.exceptions.UnauthorizedException;
import com.nestigo.systemdesign.nestigo.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private String frontEndUrl;

    @Override
    @Transactional
    public BookingDTO initializeBooking(BookingRequestDTO bookingRequestDTO) {

        log.info("initializeBooking booking for hotel: {}, room: {}. date {}-{}", bookingRequestDTO.getHotelId(),
                bookingRequestDTO.getRoomId(), bookingRequestDTO.getCheckInDate(), bookingRequestDTO.getCheckOutDate());

        HotelEntity hotel = hotelRepository.findById(bookingRequestDTO.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Hotel NOT found with id:" + bookingRequestDTO.getHotelId()));

        RoomEntity room = roomRepository.findById(bookingRequestDTO.getRoomId()).orElseThrow(() ->
                new ResourceNotFoundException("Room NOT found with id:" + bookingRequestDTO.getRoomId()));

        List<InventoryEntity> inventoryList = inventoryRepository.findAndLockInventory(room.getId(),
                bookingRequestDTO.getCheckInDate(), bookingRequestDTO.getCheckOutDate(), bookingRequestDTO.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequestDTO.getCheckInDate(), bookingRequestDTO.getCheckOutDate()) + 1;
        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available");
        }

//      reserving the room
        for (InventoryEntity inventory : inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequestDTO.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);


        //TODO: calculate the dynamic price

        //create a booking
        BookingEntity booking = BookingEntity.builder()
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
                new ResourceNotFoundException("Booking NOT found with id:" + bookingId));

        UserEntity user = getCurrentUser();

        if (!user.equals(booking.getUser())) {
            throw new UnauthorizedException("Booking does not belong to this user" + user.getId());

        }


        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has expired");

        }

        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
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
    public String initiatePayments(Long bookingId) {
//        fetching booking
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking NOT found with id:" + bookingId));

//        fetching the user
        UserEntity user = getCurrentUser();

//          checking if the user is the same
        if (!user.equals(booking.getUser())) {
            throw new UnauthorizedException("Booking does not belong to this user" + user.getId());
        }

//        checking if the booking is expired or not
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has expired");

        }

        String sessionUrl = checkoutService.getCheckoutSession(booking,
                frontEndUrl + "payments/success", frontEndUrl + "payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    public boolean hasBookingExpired(BookingEntity bookingDTO) {
        return bookingDTO.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public UserEntity getCurrentUser() {

        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
