package com.eei_test.price_service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PriceRequestEntity {
    public String item;
    public int quantity;
    public boolean vat_incl;
}
