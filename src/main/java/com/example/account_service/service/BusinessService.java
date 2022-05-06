package com.example.account_service.service;

import com.example.account_service.model.Payment;
import com.example.account_service.model.PaymentIn;
import com.example.account_service.model.PaymentOut;
import com.example.account_service.model.User;
import com.example.account_service.repos.PaymentRepository;
import com.example.account_service.repos.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessService {
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");

    public BusinessService(UserRepository userRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    public void uploadsPayrolls(List<PaymentIn> paymentIns) {
        for (PaymentIn e : paymentIns) {
            uploadPayroll(e, true);
        }
    }

    public void uploadPayroll(PaymentIn paymentIn, boolean trse) {

        if (paymentIn.getSalary() < 0 || !paymentIn.getPeriod().matches("(0[1-9]|1[0-2])-\\d{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(paymentIn.getEmployee());

        if (optionalUser.isEmpty()) {
            return;
        }

        Payment payment = new Payment();
        Optional<Payment> optionalPayment = paymentRepository.findByUserAndPeriod(optionalUser.get(), YearMonth.parse(paymentIn.getPeriod(), formatter).atDay(1));

        if (optionalPayment.isPresent()) {

            if (trse)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            payment = optionalPayment.get();
        } else {
            payment.setUser(optionalUser.get());
            payment.setPeriod(YearMonth.parse(paymentIn.getPeriod(), formatter).atDay(1));
        }

        payment.setSalary(paymentIn.getSalary());

        paymentRepository.save(payment);
    }

    public List<PaymentOut> findAllPayment(User user) {

        List<Payment> payments = paymentRepository.findByUserOrderByPeriodDesc(user);

        if (payments.isEmpty()) {
            return new ArrayList<>();
        }

        return payments.stream().map(e -> PaymentOut.of(e, user)).collect(Collectors.toList());
    }

    public PaymentOut findPayment(User user, LocalDate period) {

        Optional<Payment> optionalPayment = paymentRepository.findByUserAndPeriod(user, period);

        if (optionalPayment.isEmpty()) {
            return new PaymentOut();
        }

        return PaymentOut.of(optionalPayment.get(), user);
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
