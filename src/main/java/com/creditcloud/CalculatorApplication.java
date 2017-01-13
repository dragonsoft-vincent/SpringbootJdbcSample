package com.creditcloud;

import calculator.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@SpringBootApplication
public class CalculatorApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CalculatorApplication.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    private QueryUtils queryUtils = new QueryUtils();

    private Calculator cal = new Calculator();

    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        String loan;
        int count = 0;
        BufferedReader content = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:input.txt").getInputStream()));
        while ((loan = content.readLine()) != null) {
            loan = loan.replace(" ", "");
            count++;
            List<String> loanDetails = Arrays.asList(loan.split(","));
            if (loanDetails != null && loanDetails.size() == 3) {
                String loanId = loanDetails.get(0);
                int duration = Integer.valueOf(loanDetails.get(1));
                String dueDate = loanDetails.get(2);
                log.info("read input loanId: {}, duration: {}, dueDate: {}, count: {}", loanId, duration, dueDate, count);
                //更新 loan repayment
                Integer rate, principal;
                rate = jdbcTemplate.queryForObject(queryUtils.getRate(loanId), Integer.TYPE);
                principal = jdbcTemplate.queryForObject(queryUtils.getLoanPrincipal(loanId), Integer.TYPE);
                String interest = cal.compute(rate, duration, principal).toString();
                log.info("rate: {}, loanPrincipal: {}.", rate, principal);
                log.info("update loan repayment loanId: {}, dueDate: {}, interest: {}", loanId, dueDate, interest);
                jdbcTemplate.update(queryUtils.updateLoanDueDateAndAmountInterest(loanId, dueDate, interest));

                //更新 invest repayment
                List<Map<String, Object>> investList = jdbcTemplate.queryForList(queryUtils.getInvestIDs(loanId));
                for (Map<String, Object> single : investList) {
                    String investID = (String) single.get("ID");
                    try {
                        Integer investPrincipal = jdbcTemplate.queryForObject(queryUtils.getInvestPrincipal(investID), Integer.TYPE);
                        String interestRepayment = cal.compute(rate, duration, investPrincipal).toString();
                        log.info("update invest repayment investId: {}, dueDate: {}, interest: {}", investID, dueDate, interestRepayment);
                        jdbcTemplate.update(queryUtils.updateInvestDueDateAndAmountInterest(investID,
                                dueDate,
                                interestRepayment));
                    } catch (Exception e) {
                        log.error("update invest repayment failed investId={}", investID, e);
                    }
                }
            } else {
                log.warn("input format is not valid. line:{}", count + 1);
            }

        }
    }
}
