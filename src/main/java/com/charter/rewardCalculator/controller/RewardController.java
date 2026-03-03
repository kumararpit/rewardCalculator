package com.charter.rewardCalculator.controller;

import com.charter.rewardCalculator.dto.CustomerRewardDTO;
import com.charter.rewardCalculator.service.RewardService;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@Validated
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/calculate")
    public CompletableFuture<ResponseEntity<List<CustomerRewardDTO>>> getRewards(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) @Min(value = 1, message = "numberOfMonths must be positive") Integer numberOfMonths) {

        return rewardService.getRewardsForAllCustomers(startDate, endDate, numberOfMonths)
                .thenApply(ResponseEntity::ok);
    }
}
