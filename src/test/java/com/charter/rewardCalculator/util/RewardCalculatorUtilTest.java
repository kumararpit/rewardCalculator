package com.charter.rewardCalculator.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RewardCalculatorUtilTest {

    private final RewardCalculatorUtil util = new RewardCalculatorUtil();

    @Test
    public void testCalculatePoints_Above100() {
        assertEquals(90, util.calculatePoints(new BigDecimal("120")));
    }

    @Test
    public void testCalculatePoints_Exactly100() {
        assertEquals(50, util.calculatePoints(new BigDecimal("100")));
    }

    @Test
    public void testCalculatePoints_Between50And100() {
        assertEquals(25, util.calculatePoints(new BigDecimal("75")));
    }

    @Test
    public void testCalculatePoints_Exactly50() {
        assertEquals(0, util.calculatePoints(new BigDecimal("50")));
    }

    @Test
    public void testCalculatePoints_Below50() {
        assertEquals(0, util.calculatePoints(new BigDecimal("40")));
    }

    @Test
    public void testCalculatePoints_JustAbove100() {
        assertEquals(52, util.calculatePoints(new BigDecimal("101")));
    }

    @Test
    public void testCalculatePoints_JustAbove50() {
        assertEquals(1, util.calculatePoints(new BigDecimal("51")));
    }

    @Test
    public void testCalculatePoints_FractionalAbove100() {
        assertEquals(90, util.calculatePoints(new BigDecimal("120.50")));
    }

    @Test
    public void testCalculatePoints_FractionalBetween50And100() {
        assertEquals(25, util.calculatePoints(new BigDecimal("75.75")));
    }
}
