package com.nestigo.systemdesign.nestigo.strategy;

import com.nestigo.systemdesign.nestigo.entities.InventoryEntity;

import java.math.BigDecimal;

public class BasePriceStrategy implements PricingStrategy{

    @Override
    public BigDecimal calculatePrice(InventoryEntity inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
