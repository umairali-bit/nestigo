package com.nestigo.systemdesign.nestigo.strategy;


import com.nestigo.systemdesign.nestigo.entities.InventoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(InventoryEntity inventory) {
        PricingStrategy pricingStrategy = new BasePriceStrategy();

//  apply the additional strategies
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }

//    returning price of the inventory list
    public BigDecimal calculateTotalPrice(List<InventoryEntity> inventoryEntityList) {
        return inventoryEntityList.stream()
                .map(inventory -> calculateDynamicPricing(inventory))
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));


    }
}
