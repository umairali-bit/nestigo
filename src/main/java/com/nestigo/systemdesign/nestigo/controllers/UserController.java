package com.nestigo.systemdesign.nestigo.controllers;


import com.nestigo.systemdesign.nestigo.dtos.ProfileUpdateRequestDTO;
import com.nestigo.systemdesign.nestigo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDTO profileUpdateRequestDTO) {

        userService.updateProfile(profileUpdateRequestDTO);
        return ResponseEntity.noContent().build();
    }

}
