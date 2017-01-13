/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud;

/**
 * 类名:QueryUtils 功能：实体类/配置类/功能类<br>
 * 详细：describe the class specifical function<br>
 * 修改日期：2017-1-10<br>
 *
 * @version 1.0
 * @author Recursion <recursion at creditcloud.com>
 * 说明： 说明暂未添加
 *
 */
public class QueryUtils {

    public QueryUtils() {
    }

    /**
     * 获取利率
     *
     * @param loanId
     * @return
     */
    public String getRate(String loanId) {
        return "SELECT RATE FROM TB_LOAN WHERE ID='" + loanId + "';";
    }

    /**
     * 获取待还总金额
     *
     * @param loanId
     * @return
     */
    public String getLoanPrincipal(String loanId) {
        return "SELECT AMOUNTPRINCIPAL FROM TB_LOAN_REPAYMENT WHERE LOAN_ID='" + loanId + "';";
    }

    /**
     * 获取每笔投资金额
     *
     * @param investId
     * @return
     */
    public String getInvestPrincipal(String investId) {
        return "SELECT AMOUNTPRINCIPAL FROM TB_INVEST_REPAYMENT WHERE INVEST_ID='" + investId + "';";
    }

    public String getInvestIDs(String loanId) {
        return "SELECT ID FROM TB_INVEST WHERE LOANID='" + loanId + "';";
    }

    /**
     * 更新loan repayment的到期日
     *
     * @param loanId
     * @param dueDate
     * @return
     */
    public String updateLoanDueDate(String loanId, String dueDate) {
        return "UPDATE TB_LOAN_REPAYMENT SET DUEDATE='" + dueDate + "' WHERE LOAN_ID='" + loanId + "';";
    }

    /**
     * 更新invest repayment的利息和到期日
     *
     * @param investId
     * @param dueDate
     * @param amountInterest
     * @return
     */
    public String updateInvestDueDateAndAmountInterest(String investId, String dueDate, String amountInterest) {
        return "UPDATE TB_INVEST_REPAYMENT SET DUEDATE='"
               + dueDate + "', AMOUNTINTEREST='"
               + amountInterest + "' WHERE INVEST_ID='"
               + investId + "';";
    }

    public String updateLoanDueDateAndAmountInterest(String loanId, String dueDate, String amountInterest) {
        return "UPDATE TB_LOAN_REPAYMENT SET DUEDATE='"
               + dueDate + "', AMOUNTINTEREST='"
               + amountInterest + "' WHERE LOAN_ID='"
               + loanId + "';";
    }
}
