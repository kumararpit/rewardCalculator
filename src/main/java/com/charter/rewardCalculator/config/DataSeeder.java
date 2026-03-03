package com.charter.rewardCalculator.config;

import com.charter.rewardCalculator.model.Customer;
import com.charter.rewardCalculator.model.Transaction;
import com.charter.rewardCalculator.repository.CustomerRepository;
import com.charter.rewardCalculator.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Configuration
public class DataSeeder {

        @Bean
        CommandLineRunner initDatabase(CustomerRepository customerRepository,
                        TransactionRepository transactionRepository) {
                return args -> {
                        // Create Customers
                        Customer c1 = Customer.builder().name("Arpit").build();
                        Customer c2 = Customer.builder().name("Rahul").build();
                        Customer c3 = Customer.builder().name("Ravi").build();

                        customerRepository.saveAll(Arrays.asList(c1, c2, c3));

                        LocalDate today = LocalDate.now();

                        transactionRepository.saveAll(Arrays.asList(
                                        // Customer 1 - Last 3 months
                                        Transaction.builder().customer(c1).amount(new BigDecimal("120.00"))
                                                        .transactionDate(today.minusDays(5)).build(),
                                        Transaction.builder().customer(c1).amount(new BigDecimal("80.00"))
                                                        .transactionDate(today.minusMonths(1).minusDays(10)).build(),
                                        Transaction.builder().customer(c1).amount(new BigDecimal("50.00"))
                                                        .transactionDate(today.minusMonths(2).minusDays(15)).build(),

                                        // Customer 2 - High Spender
                                        Transaction.builder().customer(c2).amount(new BigDecimal("500.00"))
                                                        .transactionDate(today.minusDays(2)).build(),
                                        Transaction.builder().customer(c2).amount(new BigDecimal("75.00"))
                                                        .transactionDate(today.minusMonths(1).minusDays(5)).build(),

                                        // Edge cases (Customer 3)
                                        Transaction.builder().customer(c3).amount(new BigDecimal("100.00"))
                                                        .transactionDate(today.minusDays(1)).build(),
                                        Transaction.builder().customer(c3).amount(new BigDecimal("50.00"))
                                                        .transactionDate(today.minusDays(1)).build(),
                                        Transaction.builder().customer(c3).amount(new BigDecimal("51.00"))
                                                        .transactionDate(today.minusDays(1)).build(),
                                        Transaction.builder().customer(c3).amount(new BigDecimal("101.00"))
                                                        .transactionDate(today.minusDays(1)).build(),

                                        // Data for "Last 6 Months" scenario (4 months ago)
                                        Transaction.builder().customer(c1).amount(new BigDecimal("210.00"))
                                                        .transactionDate(today.minusMonths(4)).build(),

                                        // Data for "Specific Date Range" scenario (2024-11-01 to 2025-01-31)
                                        Transaction.builder().customer(c2).amount(new BigDecimal("150.00"))
                                                        .transactionDate(LocalDate.of(2024, 11, 15)).build(),
                                        Transaction.builder().customer(c2).amount(new BigDecimal("200.00"))
                                                        .transactionDate(LocalDate.of(2024, 12, 20)).build(),
                                        Transaction.builder().customer(c2).amount(new BigDecimal("120.00"))
                                                        .transactionDate(LocalDate.of(2025, 1, 10)).build()));

                        System.out.println("Demo data seeded successfully.");
                };
        }
}
