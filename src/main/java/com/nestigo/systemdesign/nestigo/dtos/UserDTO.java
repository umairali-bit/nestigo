package com.nestigo.systemdesign.nestigo.dtos;

import com.nestigo.systemdesign.nestigo.entities.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {


    private Long id;

    private String email;

    private String password;//encoded

    private String name;


    private Set<RoleEnum> roles;
}
