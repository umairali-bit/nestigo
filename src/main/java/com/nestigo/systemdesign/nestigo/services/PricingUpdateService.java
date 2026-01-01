package com.nestigo.systemdesign.nestigo.services;


import com.nestigo.systemdesign.nestigo.entities.HotelEntity;
import com.nestigo.systemdesign.nestigo.entities.HotelMinPriceEntity;
import com.nestigo.systemdesign.nestigo.entities.InventoryEntity;
import com.nestigo.systemdesign.nestigo.repositories.HotelMinPriceRepository;
import com.nestigo.systemdesign.nestigo.repositories.HotelRepository;
import com.nestigo.systemdesign.nestigo.repositories.InventoryRepository;
import com.nestigo.systemdesign.nestigo.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

    //schedular to update the inventory and hotelMinPrice every hour

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "0 0 * * * *")

    public void updatePrices() {

        int page = 0;
        int batchSize = 100;

        //getting the first page
        while (true) {
            Page<HotelEntity> hotelEntityPage = hotelRepository.findAll(PageRequest.of(page, batchSize));

            if (hotelEntityPage.isEmpty()) {
                break;
            }

            hotelEntityPage.getContent().forEach(hotelEntity -> updateHotelPrices(hotelEntity));

            page++;
        }
    }


    private void updateHotelPrices(HotelEntity hotelEntity) {

        log.info("Update hotel prices by hotel id {}", hotelEntity.getId());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        List<InventoryEntity> inventoryEntityList = inventoryRepository
                .findByHotelAndDateBetween(hotelEntity, startDate, endDate);

        updateInventoryPrices(inventoryEntityList);

        updateHotelMinPrices(hotelEntity,inventoryEntityList, startDate,endDate);


    }

    private void updateHotelMinPrices(HotelEntity hotelEntity, List<InventoryEntity> inventoryEntityList,
                                      LocalDate startDate, LocalDate endDate) {



        //Computing minimum price per day for the hotel

        Map<LocalDate, BigDecimal> dailyMinPrice = inventoryEntityList.stream()
                .collect(Collectors.groupingBy(
                        inventory -> inventory.getDate(),
                        Collectors.mapping(
                                inventory -> inventory.getPrice(),
                                Collectors.collectingAndThen(
                                        Collectors.minBy((p1,p2) -> p1.compareTo(p2)),
                                        opt -> opt.orElse(BigDecimal.ZERO)
                                )
                        )
                ));

        //Prepare HotelPrices entities in bulk
        List<HotelMinPriceEntity> hotelPrices = new ArrayList<>();
        dailyMinPrice.forEach((date, price) -> {
            HotelMinPriceEntity hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotelEntity, date)
                    .orElse(new HotelMinPriceEntity(hotelEntity, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        hotelMinPriceRepository.saveAll(hotelPrices);


    }

    private void updateInventoryPrices(List<InventoryEntity> inventoryEntityList) {

        inventoryEntityList.forEach(inventoryEntity -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventoryEntity);

            inventoryEntity.setPrice(dynamicPrice);
        });

        inventoryRepository.saveAll(inventoryEntityList);



    }


}
