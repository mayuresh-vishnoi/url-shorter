package com.mayur29.urlshortner.repository;

import com.mayur29.urlshortner.entities.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping,Long> {
    UrlMapping findLongUrlByCode(String code);
}
