package com.nestigo.systemdesign.nestigo.security;


import com.nestigo.systemdesign.nestigo.dtos.SignUpDTO;
import com.nestigo.systemdesign.nestigo.dtos.UserDTO;
import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.entities.enums.RoleEnum;
import com.nestigo.systemdesign.nestigo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.management.relation.Role;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    //take singupdata as input and return userdto
    @Transactional
    public UserDTO signUp(SignUpDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        UserEntity user = new UserEntity();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(RoleEnum.GUEST));

        UserEntity saved = userRepository.save(user);
        return modelMapper.map(saved, UserDTO.class);
    }
}
