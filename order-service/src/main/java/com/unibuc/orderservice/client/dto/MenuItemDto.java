package com.unibuc.orderservice.client.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Boolean isAvailable;
    private Long storeId;
}
