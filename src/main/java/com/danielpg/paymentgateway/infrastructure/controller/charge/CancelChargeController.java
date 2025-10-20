package com.danielpg.paymentgateway.infrastructure.controller.charge;

import com.danielpg.paymentgateway.application.charge.CancelChargeUseCase;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/charges")
public class CancelChargeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelChargeController.class);

    @Autowired
    private CancelChargeUseCase useCase;

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        LOGGER.info("Cancelando cobrança: id={}", id);
        useCase.cancelCharge(ChargeId.of(id));
        LOGGER.info("Cobrança cancelada: id={}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
