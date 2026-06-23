package com.unibuc.orderservice.client.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String phoneNumber;
}
