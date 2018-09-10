package io.examples.account.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents the account.
 *
 * @author Gary Cheng
 */
@Data
public class Account {
    private Integer id;
    private String accountNo;
    private AccoutType accountType;
    private String accountName;
    private BigDecimal openBalance;
    private boolean active;
}
