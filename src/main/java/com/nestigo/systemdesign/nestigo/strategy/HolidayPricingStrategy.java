package com.nestigo.systemdesign.nestigo.strategy;

import com.nestigo.systemdesign.nestigo.entities.InventoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {


    private final PricingStrategy wrapped;


    @Override
    public BigDecimal calculatePrice(InventoryEntity inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        boolean isTodayHoliday = true;

        if(isTodayHoliday) {
            price = price.multiply(BigDecimal.valueOf(1.25));
        }

        return price;
    }
}
