package com.nestigo.systemdesign.nestigo.dtos;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InventoryUpdateRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal  surgeFactor;
    private Boolean closed;

}
