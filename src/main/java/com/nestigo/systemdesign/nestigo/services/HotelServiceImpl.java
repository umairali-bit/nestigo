package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.HotelDTO;
import com.nestigo.systemdesign.nestigo.dtos.HotelInfoDTO;
import com.nestigo.systemdesign.nestigo.dtos.RoomDTO;
import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import com.nestigo.systemdesign.nestigo.entities.RoomEntity;
import com.nestigo.systemdesign.nestigo.exceptions.ResourceNotFoundException;
import com.nestigo.systemdesign.nestigo.repositories.HotelRepository;
import com.nestigo.systemdesign.nestigo.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{


    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;

    @Transactional
    @Override
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        log.info("Creating a new hotel with name: {}", hotelDTO.getName());

        //converting dto to entity
        HotelEntity hotelEntity = modelMapper.map(hotelDTO,HotelEntity.class);
        hotelEntity.setActive(false);
        hotelEntity = hotelRepository.save(hotelEntity);
        log.info("Created a new hotel with ID {}", hotelEntity.getId());


        //converting entity back to DTO
        return modelMapper.map(hotelEntity, HotelDTO.class);
    }

    @Override
    public HotelDTO getHotelById(Long id) {
        log.info("Fetching the hotel with ID: {}", id);
        HotelEntity hotelEntity = hotelRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Hotel NOT found with ID:" + id));



        return modelMapper.map(hotelEntity,HotelDTO.class);
    }

    @Override
    public HotelDTO updateHotel(Long id, HotelDTO hotelDTO) {
        log.info("Updating the hotel with ID: {}", id);

        //getting hotel by id
        HotelEntity existingHotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID" + id));

        modelMapper.map(hotelDTO, existingHotel);
        existingHotel.setId(id);

        HotelEntity updatedHotel = hotelRepository.save(existingHotel);
        return modelMapper.map(updatedHotel, HotelDTO.class);

    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        HotelEntity existingHotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID" + id));


        //TODO: delete future inventory for this hotel


        for(RoomEntity room: existingHotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }

        hotelRepository.deleteById(id);


        }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        log.info("Activating the hotel with ID: {}", id);
        HotelEntity existingHotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID" + id));

        existingHotel.setActive(true);
        //TODO: Create inventory for all the rooms for this hotel - done

        //assuming this has to be done once

        for(RoomEntity room: existingHotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);


        }


    }

    @Override
    public HotelInfoDTO getHotelInfoById(Long hotelId) {
        HotelEntity existingHotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID" + hotelId));

        List<RoomDTO> rooms = existingHotel.getRooms()
                .stream()
                .map((i) -> modelMapper.map(i, RoomDTO.class))
                .toList();

        return new HotelInfoDTO(modelMapper.map(existingHotel, HotelDTO.class), rooms);



    }
}
