package com.nishikatakagi.ProductDigital.repository;

import com.nishikatakagi.ProductDigital.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    Publisher findByName(String name);
}
