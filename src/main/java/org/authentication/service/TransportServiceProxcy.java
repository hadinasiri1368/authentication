package org.authentication.service;

import org.authentication.dto.ResponseDto.Person;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "TRANSPORT")
public interface TransportServiceProxcy {
    @GetMapping(path = "/api/person/{id}")
    public Person getPerson(@RequestHeader("Authorization") String token, @PathVariable Long id);
}

