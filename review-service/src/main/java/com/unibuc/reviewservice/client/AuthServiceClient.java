package com.unibuc.reviewservice.client;

import com.unibuc.reviewservice.client.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", path = "/api/v1/users")
public interface AuthServiceClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
