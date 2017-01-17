package com.creditcloud;

/**
 *
 * @author vincentchen
 * @date 17/1/9.
 */
public class Arguments {

    private String loanId;

    private int totalDays;

    private String dueDate;

    public Arguments(String loanId, int totalDays, String dueDate) {
        this.loanId = loanId;
        this.totalDays = totalDays;
        this.dueDate = dueDate;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
