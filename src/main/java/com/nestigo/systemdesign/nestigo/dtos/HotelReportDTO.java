package com.nestigo.systemdesign.nestigo.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotelReportDTO {
     private Long bookingCount;
     private BigDecimal totalRevenue;
     private BigDecimal avgRevenue;
}
