package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.BookingDTO;
import com.nestigo.systemdesign.nestigo.dtos.BookingRequestDTO;
import com.nestigo.systemdesign.nestigo.dtos.GuestDTO;
import com.nestigo.systemdesign.nestigo.dtos.HotelReportDTO;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDTO initializeBooking(BookingRequestDTO bookingRequestDTO);

    BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDtoList);

    String initiatePayments(Long bookingId);

    void capturePayments(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDTO> getAllBookingsByHotelId(Long hotelId);

    HotelReportDTO getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDTO> getMyBookings();
}
