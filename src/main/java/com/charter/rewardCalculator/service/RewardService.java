package com.charter.rewardCalculator.service;

import com.charter.rewardCalculator.dto.CustomerRewardDTO;
import com.charter.rewardCalculator.exception.TransactionNotFoundException;
import com.charter.rewardCalculator.exception.ValidationException;
import com.charter.rewardCalculator.model.Transaction;
import com.charter.rewardCalculator.repository.TransactionRepository;
import com.charter.rewardCalculator.util.RewardCalculatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final TransactionRepository transactionRepository;
    private final RewardCalculatorUtil rewardCalculatorUtil;

    public CompletableFuture<List<CustomerRewardDTO>> getRewardsForAllCustomers(LocalDate startDate,
            LocalDate endDate, Integer numberOfMonths) {

        if (startDate == null || endDate == null) {
            endDate = LocalDate.now();
            if (numberOfMonths != null) {
                startDate = endDate.minusMonths(numberOfMonths);
            } else {
                startDate = endDate.minusMonths(3);
            }
        }

        final LocalDate finalStartDate = startDate;
        final LocalDate finalEndDate = endDate;

        log.info("Calculating rewards for all customers between {} and {}", finalStartDate, finalEndDate);

        return CompletableFuture.supplyAsync(() -> {
            validateDates(finalStartDate, finalEndDate);
            List<Transaction> transactions = fetchTransactions(finalStartDate, finalEndDate);
            return processTransactions(transactions);
        });
    }

    private List<Transaction> fetchTransactions(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(startDate, endDate);
        if (transactions.isEmpty()) {
            log.info("No transactions found for the period {} to {}", startDate, endDate);
            throw new TransactionNotFoundException(
                    "No transactions found for the period " + startDate + " to " + endDate);
        }
        return transactions;
    }

    private List<CustomerRewardDTO> processTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getCustomer().getId()))
                .entrySet().stream()
                .map(entry -> createCustomerRewardDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private CustomerRewardDTO createCustomerRewardDTO(Long customerId, List<Transaction> customerTransactions) {
        Map<String, Long> monthlyRewards = calculateMonthlyRewards(customerTransactions);
        long totalRewards = monthlyRewards.values().stream().mapToLong(Long::longValue).sum();

        return CustomerRewardDTO.builder()
                .customerId(customerId)
                .monthlyRewards(monthlyRewards)
                .totalRewards(totalRewards)
                .build();
    }

    private Map<String, Long> calculateMonthlyRewards(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        Collectors.summingLong(t -> rewardCalculatorUtil.calculatePoints(t.getAmount()))));
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date.");
        }
    }
}
