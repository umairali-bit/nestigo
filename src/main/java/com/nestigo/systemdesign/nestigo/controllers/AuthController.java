package com.nestigo.systemdesign.nestigo.controllers;


import com.nestigo.systemdesign.nestigo.dtos.LoginDTO;
import com.nestigo.systemdesign.nestigo.dtos.LoginResponseDTO;
import com.nestigo.systemdesign.nestigo.dtos.SignUpDTO;
import com.nestigo.systemdesign.nestigo.dtos.UserDTO;
import com.nestigo.systemdesign.nestigo.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<UserDTO> signUp(@Valid @RequestBody SignUpDTO signUpDTO) {

        return new ResponseEntity<>(authService.signUp(signUpDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login (@RequestBody LoginDTO loginDTO, HttpServletResponse response) {

        String[] tokens = authService.login(loginDTO);

        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return  new ResponseEntity<>(new LoginResponseDTO(tokens[0]), HttpStatus.OK);
    }

}
