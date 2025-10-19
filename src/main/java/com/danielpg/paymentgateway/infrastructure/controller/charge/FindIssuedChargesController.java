package com.danielpg.paymentgateway.infrastructure.controller.charge;

import com.danielpg.paymentgateway.application.charge.FindIssuedChargesUseCase;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/charges/issued")
public class FindIssuedChargesController {

    @Autowired
    private FindIssuedChargesUseCase useCase;

    @GetMapping
    public ResponseEntity<FindIssuedChargesUseCase.Response> get(
            @RequestParam(value = "statuses", required = false) String statusCsv) {
        var statuses = ChargeStatus.fromCsv(statusCsv);
        var response = useCase.find(statuses);
        return ResponseEntity.ok(response);
    }
}