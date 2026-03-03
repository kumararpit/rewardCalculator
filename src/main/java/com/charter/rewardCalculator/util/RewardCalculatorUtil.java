package com.charter.rewardCalculator.util;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class RewardCalculatorUtil {

    public long calculatePoints(BigDecimal amount) {
        if (amount == null || amount.compareTo(new BigDecimal("50")) <= 0) {
            return 0;
        }

        long points = 0;
        BigDecimal fifty = new BigDecimal("50");
        BigDecimal hundred = new BigDecimal("100");

        if (amount.compareTo(hundred) > 0) {
            points += (amount.subtract(hundred).longValue() * 2);
            points += 50; // Points for amount between 50 and 100
        } else {
            points += (amount.subtract(fifty).longValue());
        }

        return points;
    }
}
