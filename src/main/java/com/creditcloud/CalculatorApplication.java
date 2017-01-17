package com.creditcloud;

import calculator.Calculator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@SpringBootApplication
public class CalculatorApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CalculatorApplication.class);

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    private Calculator cal = new Calculator();

    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

        String content = IOUtils.toString(resourceLoader.getResource("classpath:input.txt").getInputStream());
        Pair<Map<String, Arguments>, List<String>> argumentsResult = getArguments(content);

        Map<String, Arguments> argumentsMap = argumentsResult.getLeft();
        List<String> loanIds = argumentsResult.getRight();
        Map<String, Integer> rateMap = getRate(loanIds);
        Map<String, BigDecimal> loanPrincipalMap = getLoanPrincipal(loanIds);

        if (CollectionUtils.isNotEmpty(loanIds)) {
            for (String loanId : loanIds) {
                int rate = rateMap.get(loanId);
                int totalDays = argumentsMap.get(loanId).getTotalDays();
                BigDecimal loanPrincipal = loanPrincipalMap.get(loanId);
                String dueDate = argumentsMap.get(loanId).getDueDate();
                log.info("rate: {}, totalDays:{}, loanPrincipal: {}.", rate, totalDays, loanPrincipal);

                String loanInterest = cal.compute(rate, totalDays, loanPrincipal).toString();
                log.info("Update loan repayment loanId: {}, dueDate: {}, interest: {}", loanId, dueDate, loanInterest);
                updateDueDateAndInterestForLoanRepay(loanId, dueDate, loanInterest);

                List<String> investIds = getInvestsByLoan(loanId);
                Map<String, BigDecimal> investPrincipalMap = getInvestPrincipal(investIds);

                if (CollectionUtils.isNotEmpty(investIds)) {
                    for (String investId : investIds) {
                        String investInterest = cal.compute(rate, totalDays, investPrincipalMap.get(investId)).toString();
                        log.info("update invest repayment investId: {}, dueDate: {}, interest: {}", investId, dueDate, investInterest);
                        updateDueDateAndInterestForInvestRepay(investId, dueDate, investInterest);
                    }
                }
            }
        }
    }

    private List<String> getInvestsByLoan(String loanId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("loanId", loanId);

        List<String> investIds = Lists.newArrayList();
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT ID FROM TB_INVEST WHERE LOANID =:loanId", parameters);
        for (Map<String, Object> single : result) {
            investIds.add((String) single.get("ID"));
        }

        return investIds;
    }

    /**
     * @param loanId
     * @param dueDate
     * @param amountInterest
     */
    private void updateDueDateAndInterestForLoanRepay(String loanId, String dueDate, String amountInterest) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", loanId);
        parameters.addValue("dueDate", dueDate);
        parameters.addValue("amountInterest", amountInterest);

        int success = jdbcTemplate.update("UPDATE TB_LOAN_REPAYMENT SET DUEDATE=:dueDate, AMOUNTINTEREST=:amountInterest WHERE LOAN_ID=:id", parameters);
        log.info("Success update loan repayment count" + success);
    }

    /**
     * @param investId
     * @param dueDate
     * @param amountInterest
     */
    private void updateDueDateAndInterestForInvestRepay(String investId, String dueDate, String amountInterest) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", investId);
        parameters.addValue("dueDate", dueDate);
        parameters.addValue("amountInterest", amountInterest);

        int success = jdbcTemplate.update("UPDATE TB_INVEST_REPAYMENT SET DUEDATE=:dueDate, AMOUNTINTEREST=:amountInterest WHERE INVEST_ID=:id", parameters);
        log.info("Success update invest repayment count" + success);
    }

    /**
     * @param loanIds
     * @return
     */
    private Map<String, BigDecimal> getLoanPrincipal(List<String> loanIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", loanIds);

        Map<String, BigDecimal> principalMap = Maps.newHashMap();
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT LOAN_ID, AMOUNTPRINCIPAL FROM TB_LOAN_REPAYMENT WHERE LOAN_ID IN (:ids)", parameters);
        for (Map<String, Object> single : result) {
            principalMap.put((String) single.get("LOAN_ID"), (BigDecimal) single.get("AMOUNTPRINCIPAL"));
        }
        return principalMap;
    }

    /**
     * @param loanIds
     * @return
     */
    private Map<String, BigDecimal> getInvestPrincipal(List<String> loanIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", loanIds);

        Map<String, BigDecimal> principalMap = Maps.newHashMap();
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT INVEST_ID, AMOUNTPRINCIPAL FROM TB_INVEST_REPAYMENT WHERE INVEST_ID IN (:ids)", parameters);
        for (Map<String, Object> single : result) {
            principalMap.put((String) single.get("INVEST_ID"), (BigDecimal) single.get("AMOUNTPRINCIPAL"));
        }
        return principalMap;
    }

    /**
     * @param loanIds
     * @return
     */
    private Map<String, Integer> getRate(List<String> loanIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", loanIds);

        Map<String, Integer> rateMap = Maps.newHashMap();
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT ID, RATE FROM TB_LOAN WHERE ID IN (:ids)", parameters);
        for (Map<String, Object> single : result) {
            rateMap.put((String) single.get("ID"), (Integer) single.get("RATE"));
        }
        return rateMap;
    }

    /**
     * parse input
     *
     * @param source
     * @return
     */
    public static Pair<Map<String, Arguments>, List<String>> getArguments(String source) {
        Map<String, Arguments> argumentsMap = Maps.newHashMap();
        List<String> loanIds = Lists.newArrayList();
        if (StringUtils.isNotEmpty(source)) {
            List<String> args = Arrays.asList(source.split("\n"));
            if (CollectionUtils.isNotEmpty(args)) {
                int lineNum = 0;
                for (String arg : args) {
                    String[] line = arg.split(",");
                    if (line.length == 3) {
                        String loanId = line[0];
                        int totalDays = Integer.valueOf(line[1]).intValue();
                        String dueDate = line[2];
                        log.info("Read input loanId: {}, totalDays: {}, dueDate: {}, lineNum: {}", loanId, totalDays, dueDate, lineNum);
                        Arguments arguments = new Arguments(loanId, totalDays, dueDate);
                        argumentsMap.put(loanId, arguments);
                        loanIds.add(loanId);
                    } else {
                        log.warn("Input format is not valid. line number is {}", lineNum);
                    }
                    lineNum++;
                }
            }
        } else {
            log.info("Input file is empty");
        }
        return Pair.of(argumentsMap, loanIds);
    }
}
