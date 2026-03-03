package com.charter.rewardCalculator.service;

import com.charter.rewardCalculator.dto.CustomerRewardDTO;
import com.charter.rewardCalculator.exception.TransactionNotFoundException;
import com.charter.rewardCalculator.model.Customer;
import com.charter.rewardCalculator.model.Transaction;
import com.charter.rewardCalculator.repository.TransactionRepository;
import com.charter.rewardCalculator.util.RewardCalculatorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RewardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RewardCalculatorUtil rewardCalculatorUtil;

    @InjectMocks
    private RewardService rewardService;

    private Customer c1;
    private Customer c2;

    @BeforeEach
    void setUp() {
        c1 = Customer.builder().id(1L).name("Customer 1").build();
        c2 = Customer.builder().id(2L).name("Customer 2").build();
    }

    @Test
    void testGetRewardsForAllCustomers_DatesNull() throws Exception {
        // When dates are null, it should default to 3 months.
        when(transactionRepository.findByTransactionDateBetween(any(), any())).thenReturn(Collections.emptyList());
        
        ExecutionException exception = assertThrows(ExecutionException.class, 
            () -> rewardService.getRewardsForAllCustomers(null, null, null).get());
        
        assertTrue(exception.getCause() instanceof TransactionNotFoundException);
    }

    @Test
    void testGetRewardsForAllCustomers_StartAfterEnd() {
        assertThrows(ExecutionException.class,
                () -> rewardService.getRewardsForAllCustomers(LocalDate.now(),
                        LocalDate.now().minusDays(1), null).get());
    }

    @Test
    void testGetRewardsForAllCustomers_NoTransactions() throws Exception {
        when(transactionRepository.findByTransactionDateBetween(any(), any())).thenReturn(Collections.emptyList());

        ExecutionException exception = assertThrows(ExecutionException.class, 
            () -> rewardService.getRewardsForAllCustomers(
                LocalDate.now().minusMonths(1), LocalDate.now(), null).get());
        
        assertTrue(exception.getCause() instanceof TransactionNotFoundException);
    }

    @Test
    void testGetRewardsForAllCustomers_Success() throws Exception {
        Transaction t1 = Transaction.builder().customer(c1).amount(new BigDecimal("120"))
                .transactionDate(LocalDate.now()).build();
        Transaction t2 = Transaction.builder().customer(c2).amount(new BigDecimal("80"))
                .transactionDate(LocalDate.now()).build();

        when(transactionRepository.findByTransactionDateBetween(any(), any()))
                .thenReturn(Arrays.asList(t1, t2));
        when(rewardCalculatorUtil.calculatePoints(new BigDecimal("120"))).thenReturn(90L);
        when(rewardCalculatorUtil.calculatePoints(new BigDecimal("80"))).thenReturn(30L);

        List<CustomerRewardDTO> result = rewardService.getRewardsForAllCustomers(
                LocalDate.now().minusMonths(1), LocalDate.now(), null).get();

        assertEquals(2, result.size());

        CustomerRewardDTO res1 = result.stream().filter(r -> r.getCustomerId().equals(1L)).findFirst().get();
        assertEquals(90, res1.getTotalRewards());

        CustomerRewardDTO res2 = result.stream().filter(r -> r.getCustomerId().equals(2L)).findFirst().get();
        assertEquals(30, res2.getTotalRewards());
    }

    @Test
    void testGetRewardsForAllCustomers_WithNumberOfMonths() throws Exception {
        when(transactionRepository.findByTransactionDateBetween(any(), any())).thenReturn(Collections.emptyList());
        
        ExecutionException exception = assertThrows(ExecutionException.class, 
            () -> rewardService.getRewardsForAllCustomers(null, null, 6).get());
        
        assertTrue(exception.getCause() instanceof TransactionNotFoundException);
    }

    @Test
    void testGetRewardsForAllCustomers_SpecificDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        Transaction t1 = Transaction.builder().customer(c1).amount(new BigDecimal("120"))
                .transactionDate(LocalDate.of(2024, 11, 15)).build();
        Transaction t2 = Transaction.builder().customer(c1).amount(new BigDecimal("80"))
                .transactionDate(LocalDate.of(2024, 12, 10)).build();
        Transaction t3 = Transaction.builder().customer(c1).amount(new BigDecimal("150"))
                .transactionDate(LocalDate.of(2025, 1, 5)).build();

        when(transactionRepository.findByTransactionDateBetween(startDate, endDate))
                .thenReturn(Arrays.asList(t1, t2, t3));
        when(rewardCalculatorUtil.calculatePoints(new BigDecimal("120"))).thenReturn(90L);
        when(rewardCalculatorUtil.calculatePoints(new BigDecimal("80"))).thenReturn(30L);
        when(rewardCalculatorUtil.calculatePoints(new BigDecimal("150"))).thenReturn(150L);

        List<CustomerRewardDTO> result = rewardService.getRewardsForAllCustomers(startDate, endDate, null)
                .get();

        assertEquals(1, result.size());
        CustomerRewardDTO res = result.get(0);
        assertEquals(270, res.getTotalRewards());
    }
}
