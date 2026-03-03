package com.charter.rewardCalculator.controller;

import com.charter.rewardCalculator.dto.CustomerRewardDTO;
import com.charter.rewardCalculator.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
public class RewardControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private RewardService rewardService;

        @Test
        void testCalculateRewards_Success() throws Exception {
                CustomerRewardDTO c1 = CustomerRewardDTO.builder()
                                .customerId(1L)
                                .monthlyRewards(Collections.singletonMap("January", 90L))
                                .totalRewards(90)
                                .build();

                when(rewardService.getRewardsForAllCustomers(any(), any(), any()))
                                .thenReturn(CompletableFuture.completedFuture(Arrays.asList(c1)));

                mockMvc.perform(get("/api/v1/rewards/calculate?startDate=2025-01-01&endDate=2025-03-31")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(request().asyncStarted())
                                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                                                .andExpect(status().isOk())
                                                .andExpect(jsonPath("$[0].customerId").value(1))
                                                .andExpect(jsonPath("$[0].totalRewards").value(90)));
        }

        @Test
        void testCalculateRewards_NoParams_Returns200() throws Exception {
                when(rewardService.getRewardsForAllCustomers(any(), any(), any()))
                                .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

                mockMvc.perform(get("/api/v1/rewards/calculate")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(request().asyncStarted())
                                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                                                .andExpect(status().isOk()));
        }

        @Test
        void testCalculateRewards_WithNumberOfMonths_Returns200() throws Exception {
                when(rewardService.getRewardsForAllCustomers(any(), any(), any()))
                                .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

                mockMvc.perform(get("/api/v1/rewards/calculate?numberOfMonths=6")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(request().asyncStarted())
                                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                                                .andExpect(status().isOk()));
        }

        @Test
        void testCalculateRewards_InvalidDateFormat_Returns400() throws Exception {
                mockMvc.perform(get("/api/v1/rewards/calculate?startDate=abc&endDate=def")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        void testCalculateRewards_SpecificDateRange() throws Exception {
                CustomerRewardDTO c1 = CustomerRewardDTO.builder()
                                .customerId(1L)
                                .monthlyRewards(Collections.singletonMap("November", 90L))
                                .totalRewards(90)
                                .build();

                when(rewardService.getRewardsForAllCustomers(any(), any(), any()))
                                .thenReturn(CompletableFuture.completedFuture(Arrays.asList(c1)));

                mockMvc.perform(get("/api/v1/rewards/calculate?startDate=2024-11-01&endDate=2025-01-31")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(request().asyncStarted())
                                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                                                .andExpect(status().isOk())
                                                .andExpect(jsonPath("$[0].customerId").value(1))
                                                .andExpect(jsonPath("$[0].totalRewards").value(90)));
        }
}
