package com.example.nexio.repository;

import com.example.nexio.model.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceItem, Long> {
}
