package com.nestigo.systemdesign.nestigo.dtos;

import com.nestigo.systemdesign.nestigo.entities.enums.Gender;
import com.nestigo.systemdesign.nestigo.entities.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDTO {


    private Long id;

    private String email;

    private String password;//encoded

    private String name;

    private Gender gender;

    private LocalDate dateOfBirth;


    private Set<RoleEnum> roles;
}
