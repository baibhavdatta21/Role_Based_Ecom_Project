package com.ecommerce.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@Entity
@Table(name="address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String street;
    String city;
    String state;
    String country;
    String Zipcode;

    public Address(String street, String city, String state, String country, String zipcode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        Zipcode = zipcode;
    }
}
