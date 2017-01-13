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
 *
 * @author Recursion <recursion at creditcloud.com>
 */
public class Calculator {

    public Calculator() {
    }

    public BigDecimal compute(int rate, int duration, int principalValue) {

        BigDecimal rateScale = new BigDecimal(10000);

        MathContext mcPrivate = new MathContext(16, RoundingMode.HALF_EVEN);

        BigDecimal principal = new BigDecimal(principalValue);
        //now get rates
        BigDecimal rateYear = new BigDecimal(rate).divide(rateScale, mcPrivate);
        BigDecimal rateDay = rateYear.divide(new BigDecimal(365), mcPrivate);
        //dealing with different methods
        BigDecimal interest, amortizedInterest, amortizedPrincipal, outstandingPrincipal;
        interest = principal.multiply(rateDay).multiply(new BigDecimal(duration));

        //ceilling the interest
        return interest.setScale(2, RoundingMode.HALF_EVEN);
    }
}
