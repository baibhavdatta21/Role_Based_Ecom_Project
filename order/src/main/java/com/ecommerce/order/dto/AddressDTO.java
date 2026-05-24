package com.ecommerce.order.dto;

import lombok.Data;

@Data
public class AddressDTO {
    String street;
    String city;
    String state;
    String country;
    String Zipcode;
}

