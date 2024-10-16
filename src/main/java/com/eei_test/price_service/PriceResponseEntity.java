package com.eei_test.price_service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PriceResponseEntity {
    public int quantity;
    public float tot_price;
}
