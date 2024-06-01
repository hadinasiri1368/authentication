package org.authentication.service;

import org.authentication.dto.ResponseDto.Person;
import org.authentication.dto.ResponseDto.UserPersonDto;
import org.authentication.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "TRANSPORT")
public interface TransportServiceProxcy {
    @GetMapping(path = "/transport/person/{id}")
    public Person getPerson(@RequestHeader("Authorization") String token, @RequestHeader("X-UUID") String uuid, @PathVariable Long id);

    @PostMapping(path = "/transport/personUser")
    public List<UserPersonDto> getUserPerson(@RequestHeader("Authorization") String token, @RequestHeader("X-UUID") String uuid, @RequestBody List<User> users);
}

