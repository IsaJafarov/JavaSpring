package com.example.spring.repository;

import com.example.spring.domain.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    @Query("UPDATE business SET name=?1, info=?2 WHERE id=?3")
    void update(String name, String info, long id);

}
