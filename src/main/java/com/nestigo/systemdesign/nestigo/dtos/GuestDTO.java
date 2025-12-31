package com.nestigo.systemdesign.nestigo.dtos;


import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDTO {

    private Long id;

    private UserEntity user;


    private String name;


    private Gender gender;

    private Integer age;
}
