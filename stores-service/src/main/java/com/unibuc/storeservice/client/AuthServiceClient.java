package com.unibuc.storeservice.client;

import com.unibuc.storeservice.client.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client used to reach the auth-service through the discovery-based
 * load balancer (lb://auth-service). The caller's bearer token is forwarded by
 * {@link FeignAuthForwardingConfig}.
 */
@FeignClient(name = "auth-service", path = "/api/v1/users")
public interface AuthServiceClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
