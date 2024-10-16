package com.eei_test.price_service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table (name = "items_table")
public class MyItemEntity {
    @Id
    public String name;
    public String bar_code;
    public String country_of_origin;
    public Integer tot_quantity;
    public boolean in_stock;
    public Date exp_date;
    public Integer price;

}
