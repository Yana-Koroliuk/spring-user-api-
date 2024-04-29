package com.koroliuk.userapi.repository;

import com.koroliuk.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByBirthDateBetween(LocalDate start, LocalDate end);
    List<User> findByBirthDateAfter(LocalDate start);
    List<User> findByBirthDateBefore(LocalDate end);
}
