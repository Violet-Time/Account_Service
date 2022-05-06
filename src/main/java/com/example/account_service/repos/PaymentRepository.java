package com.example.account_service.repos;

import com.example.account_service.model.Payment;
import com.example.account_service.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findByUserAndPeriod(User user, LocalDate period);
    List<Payment> findByUserOrderByPeriodDesc(User user);
}
