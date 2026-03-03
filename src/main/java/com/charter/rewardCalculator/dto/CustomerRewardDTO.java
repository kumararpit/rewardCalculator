package com.charter.rewardCalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRewardDTO {
    private Long customerId;
    private Map<String, Long> monthlyRewards;
    private long totalRewards;
}
