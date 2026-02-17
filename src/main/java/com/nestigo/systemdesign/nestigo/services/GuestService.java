package com.nestigo.systemdesign.nestigo.services;


import com.nestigo.systemdesign.nestigo.dtos.BookingDTO;
import com.nestigo.systemdesign.nestigo.dtos.GuestDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface GuestService {

    List<GuestDTO> getAllGuests();

    GuestDTO addNewGuest(GuestDTO guestDTO);
}
