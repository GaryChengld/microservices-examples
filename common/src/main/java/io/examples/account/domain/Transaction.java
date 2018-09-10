package io.examples.account.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Transaction {
    private Integer transactionId;
    private Integer accountId;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private Date date;
}
