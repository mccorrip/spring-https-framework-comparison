package com.eei_test.price_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyItemRepository extends JpaRepository<MyItemEntity, String>{

}
