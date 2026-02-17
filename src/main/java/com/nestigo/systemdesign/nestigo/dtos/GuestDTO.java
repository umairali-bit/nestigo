package com.nestigo.systemdesign.nestigo.dtos;


import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestDTO {

    private Long id;


    private String name;


    private Gender gender;

    private LocalDate dateOfBirth;
}
