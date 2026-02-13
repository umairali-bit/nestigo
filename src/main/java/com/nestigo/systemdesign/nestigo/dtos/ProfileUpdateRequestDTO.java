package com.nestigo.systemdesign.nestigo.dtos;


import com.nestigo.systemdesign.nestigo.entities.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDTO {
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;

}
