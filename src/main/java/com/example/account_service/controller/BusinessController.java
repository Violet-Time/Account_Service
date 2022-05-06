package com.example.account_service.controller;

import com.example.account_service.model.PaymentIn;
import com.example.account_service.model.User;
import com.example.account_service.service.BusinessService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    /*
    * Gives access to the employee's payrolls.
    * Return a response in the JSON format:
    * {
    *    "name": "<user name>",
    *    "lastname": "<user lastname>",
    *    "period": "<name of month-YYYY>",
    *    "salary": "X dollar(s) Y cent(s)"
    * }
    * If the parameter period is not specified, the endpoint provides information
    * about the employee's salary for each period from the database as an array
    * of objects in descending order by date.
    */
    @GetMapping("/empl/payment")
    public Object getEmplPay(@Valid @Pattern(regexp = "(0[1-9]|1[0-2])-\\d{4}")
                             @RequestParam(name = "period", required = false)
                             String period,
                             @AuthenticationPrincipal User user) {

        if (period == null) {
            return businessService.findAllPayment(user);
        }

        if (!period.matches("(0[1-9]|1[0-2])-\\d{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return businessService.findPayment(user, YearMonth.parse(period, businessService.getFormatter()).atDay(1));
    }

    /*
    * Uploads payrolls.
    * Accepts data in the JSON format:
    * [
    *     {
    *         "employee": "<user email>",
    *         "period": "<mm-YYYY>",
    *         "salary": <Long>
    *     },
    *     {
    *         "employee": "<user1 email>",
    *         "period": "<mm-YYYY>",
    *         "salary": <Long>
    *     },
    *     ...
    *     {
    *         "employee": "<userN email>",
    *         "period": "<mm-YYYY>",
    *         "salary": <Long>
    *     }
    * ]
    * Return a response in the JSON format:
    * {
    *    "status": "Added successfully!"
    * }
    */
    @PostMapping("/acct/payments")
    public Map<String, String> postAcctPay(@RequestBody List<@Valid PaymentIn> paymentsForm) {
        businessService.uploadsPayrolls(paymentsForm);
        return Map.of("status", "Added successfully!");
    }

    /*
    * Updates payment information.
    * Accepts data in the JSON format:
    * {
    *     "employee": "<user email>",
    *     "period": "<mm-YYYY>",
    *     "salary": <Long>
    * }
    * Return a response in the JSON format:
    * {
    *    "status": "Updated successfully!"
    * }
    */
    @PutMapping("/acct/payments")
    public Map<String, String> putAcctPay(@Valid @RequestBody PaymentIn paymentIn) {
        businessService.uploadPayroll(paymentIn, false);
        return Map.of("status", "Updated successfully!");
    }
}
