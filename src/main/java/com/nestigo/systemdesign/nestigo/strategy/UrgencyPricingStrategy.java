package com.nestigo.systemdesign.nestigo.strategy;

import com.nestigo.systemdesign.nestigo.entities.InventoryEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;


    @Override
    public BigDecimal calculatePrice(InventoryEntity inventory) {

        BigDecimal price = wrapped.calculatePrice(inventory);

        LocalDate today = LocalDate.now();

        LocalDate inventoryDate = inventory.getDate();

        boolean isWithInSevenDays = !inventoryDate.isBefore(today) && inventoryDate.isBefore(today.plusDays(7));

        if(isWithInSevenDays){
            price = price.multiply(BigDecimal.valueOf(1.15));
        }

        return price;
    }
}
