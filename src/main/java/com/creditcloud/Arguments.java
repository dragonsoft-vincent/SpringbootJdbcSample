package com.creditcloud;

import java.math.BigDecimal;

/**
 *
 * @author vincentchen
 * @date 17/1/9.
 */
public class Arguments {

    private String loanId;

    private String dueDate;
    
    private BigDecimal amountInterest;

    public BigDecimal getAmountInterest() {
        return amountInterest;
    }

    public void setAmountInterest(BigDecimal amountInterest) {
        this.amountInterest = amountInterest;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
