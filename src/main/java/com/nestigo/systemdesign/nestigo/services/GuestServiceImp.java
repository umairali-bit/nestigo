package com.nestigo.systemdesign.nestigo.services;


import com.nestigo.systemdesign.nestigo.dtos.BookingDTO;
import com.nestigo.systemdesign.nestigo.dtos.GuestDTO;
import com.nestigo.systemdesign.nestigo.entities.GuestEntity;
import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.repositories.GuestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
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

    @Override
    public void updateGuest(Long guestId, GuestDTO guestDTO) {
        log.info("Updating guest with ID: {}", guestId);

        GuestEntity guest = guestRepository.findById(guestId)
                .orElseThrow(()-> new EntityNotFoundException("Guest with ID: " + guestId));

        UserEntity user = getCurrentUser();
        if(!user.equals(guest.getUser())) {
            throw new AccessDeniedException("You are not allowed to update this guest");
        }
        modelMapper.map(guestDTO, guest);

        guest.setUser(user);
        guest.setId(guestId);
        guestRepository.save(guest);


        log.info("Guest with ID: {} updated successfully", guestId);



    }

}
