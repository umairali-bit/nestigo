package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.entities.UserEntity;

import java.util.Optional;

public interface UserService {

   UserEntity getUserById(Long id);
}
