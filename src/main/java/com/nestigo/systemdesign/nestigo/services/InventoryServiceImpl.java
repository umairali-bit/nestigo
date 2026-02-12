package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.*;
import com.nestigo.systemdesign.nestigo.entities.*;
import com.nestigo.systemdesign.nestigo.exceptions.ResourceNotFoundException;
import com.nestigo.systemdesign.nestigo.repositories.HotelMinPriceRepository;
import com.nestigo.systemdesign.nestigo.repositories.InventoryRepository;
import com.nestigo.systemdesign.nestigo.repositories.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.nestigo.systemdesign.nestigo.utils.AppUtils.getCurrentUser;


@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;


    @Override
    public void initializeRoomForAYear(RoomEntity room) {
        LocalDate today = LocalDate.now();

        LocalDate endDate = today.plusYears(1);

        for (; !today.isAfter(endDate); today = today.plusDays(1)) {
            InventoryEntity inventory = InventoryEntity.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);

        }

    }

    @Override
    public void deleteAllInventories(RoomEntity room) {
        log.info("deleteAllInventories with id:{}", room.getId());
        inventoryRepository.deleteByRoom(room);

    }

    @Override
    public Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("searchHotels for {} city, from {} to {}", hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getPageSize());
        long dateCount = ChronoUnit.DAYS.between(
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate().plusDays(1)
        );

//      business logic - 90 days
        Page<HotelPriceDTO> hotelPage =
                hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                        hotelSearchRequest.getStartDate(),
                        hotelSearchRequest.getEndDate(),
                        hotelSearchRequest.getRoomCount(),
                        dateCount, pageable);
        return hotelPage;
    }

    @Override
    public List<InventoryDTO> getAllInventoryByRoom(Long roomId) {
        log.info("getAllInventoryByRoom with id:{}", roomId);

        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        UserEntity user = getCurrentUser();
        if (room.getHotel().getOwner() == null || !user.getId().equals(room.getHotel().getOwner().getId())) {
            throw new AccessDeniedException("You are not the owner of this hotel");
        }

        return inventoryRepository.findByRoomOrderByDate(room)
                .stream()
                .map((element) -> modelMapper.map(element, InventoryDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, InventoryUpdateRequestDTO inventoryUpdateRequestDTO) {
        log.info("Updating room inventory with id:{}", roomId);

        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        UserEntity user = getCurrentUser();
        if (room.getHotel().getOwner() == null || !user.getId().equals(room.getHotel().getOwner().getId())) {
            throw new AccessDeniedException("You are not the owner of this hotel");
        }

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, inventoryUpdateRequestDTO.getStartDate(),
                inventoryUpdateRequestDTO.getEndDate());

        inventoryRepository.updateInventory(roomId, inventoryUpdateRequestDTO.getStartDate(),
                inventoryUpdateRequestDTO.getEndDate(), inventoryUpdateRequestDTO.getClosed(),
                inventoryUpdateRequestDTO.getSurgeFactor());


    }
}
