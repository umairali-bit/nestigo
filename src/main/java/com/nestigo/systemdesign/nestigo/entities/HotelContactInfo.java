package com.nestigo.systemdesign.nestigo.entities;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable //Embedded in HotelEntity
public class HotelContactInfo {

    private String address;
    private String phoneNumber;
    private String email;
    private String location;

}
