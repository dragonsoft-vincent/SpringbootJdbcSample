/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * calculate interest
 *
 * @author Recursion <recursion at creditcloud.com>
 */
public class Calculator {

    private final static int DAYS_PER_YEAR = 365;

    public Calculator() {
    }

    /**
     * @param rate           interest rate per year. eg:2400-->24% per year
     * @param totalDays      total days
     * @param principal      principal
     * @return
     */
    public BigDecimal compute(int rate, int totalDays, BigDecimal principal) {

        BigDecimal rateScale = new BigDecimal(10000);

        MathContext mcPrivate = new MathContext(16, RoundingMode.HALF_EVEN);

        BigDecimal rateYear = new BigDecimal(rate).divide(rateScale, mcPrivate);

        BigDecimal rateDay = rateYear.divide(new BigDecimal(DAYS_PER_YEAR), mcPrivate);

        BigDecimal interest = principal.multiply(rateDay).multiply(new BigDecimal(totalDays));

        return interest.setScale(2, RoundingMode.HALF_EVEN);
    }
}
