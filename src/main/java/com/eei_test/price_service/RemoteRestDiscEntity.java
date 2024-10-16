package com.eei_test.price_service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RemoteRestDiscEntity {
    public float discount;
    public String item;
    public int quantity;
    public boolean applicable_in_eu;
    public float shipping_cost;
    public int shipping_time_days;
    public List<String> related_items;
}
