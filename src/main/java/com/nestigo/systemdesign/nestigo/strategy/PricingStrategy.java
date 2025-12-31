package com.nestigo.systemdesign.nestigo.strategy;

import com.nestigo.systemdesign.nestigo.entities.InventoryEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PricingStrategy {

    BigDecimal calculatePrice(InventoryEntity inventory);
}


