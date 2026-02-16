package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.dtos.ProfileUpdateRequestDTO;
import com.nestigo.systemdesign.nestigo.dtos.UserDTO;
import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.nestigo.systemdesign.nestigo.utils.AppUtils.getCurrentUser;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO) {

        UserEntity user = getCurrentUser();

        if (profileUpdateRequestDTO.getName() != null) user.setName(profileUpdateRequestDTO.getName());
        if(profileUpdateRequestDTO.getDateOfBirth() != null) user.setDateOfBirth(profileUpdateRequestDTO.getDateOfBirth());
        if(profileUpdateRequestDTO.getGender() != null) user.setGender(profileUpdateRequestDTO.getGender());

        userRepository.save(user);

    }

    @Override
    public UserDTO getMyProfile() {

        UserEntity user = getCurrentUser();

        log.info("Getting the profile for the User with id: {}", getCurrentUser().getId());
        return modelMapper.map(user,UserDTO.class);

    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new BadCredentialsException("User not found with username: " + username));
    }
}
