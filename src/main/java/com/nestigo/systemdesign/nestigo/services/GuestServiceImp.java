package com.nestigo.systemdesign.nestigo.services;


import com.nestigo.systemdesign.nestigo.dtos.BookingDTO;
import com.nestigo.systemdesign.nestigo.dtos.GuestDTO;
import com.nestigo.systemdesign.nestigo.entities.GuestEntity;
import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.repositories.GuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.nestigo.systemdesign.nestigo.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImp implements GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<GuestDTO> getAllGuests() {
        UserEntity user = getCurrentUser();
        log.info("Fetching all guests of user with id: {}", user.getId());
        List<GuestEntity> guests = guestRepository.findByUser(user);
        return guests.stream()
                .map(guest -> modelMapper.map(guest, GuestDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public GuestDTO addNewGuest(GuestDTO guestDTO) {
        UserEntity user = getCurrentUser();
        log.info("Adding new guest: {}", guestDTO);
        GuestEntity guest = modelMapper.map(guestDTO, GuestEntity.class);
        guest.setUser(user);
        GuestEntity savedGuest = guestRepository.save(guest);
        log.info("Guest added with ID: {}", savedGuest.getId());
        return modelMapper.map(savedGuest, GuestDTO.class);


    }

}
