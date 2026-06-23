package com.unibuc.orderservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemDto {
    private Long id;
    private Long storeId;
    private String name;
    private BigDecimal price;
    private Boolean isAvailable;
}
