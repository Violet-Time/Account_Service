package com.example.account_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOut {
    private String name;
    private String lastname;
    private String period;
    private String salary;

    public static PaymentOut of(Payment payment, User user) {
        PaymentOut paymentOut = new PaymentOut();
        paymentOut.setName(user.getName());
        paymentOut.setLastname(user.getLastname());
        paymentOut.setPeriod(payment.getPeriod().format(DateTimeFormatter.ofPattern("MMMM-yyyy")));
        paymentOut.setSalary((payment.getSalary() / 100) + " dollar(s) " + (payment.getSalary() - (payment.getSalary() / 100) * 100) + " cent(s)");
        return paymentOut;
    }
}
