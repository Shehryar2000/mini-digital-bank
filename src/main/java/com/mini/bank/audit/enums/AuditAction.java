package com.mini.bank.audit.enums;

public enum AuditAction {

    //Auth
    LOGIN,
    REGISTER,
    PASSWORD_CHANGE,

    //User
    USER_ENABLED,
    USER_DISABLED,
    ROLE_CHANGED,
    USER_FETCH,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,

    //Customer
    CUSTOMER_FETCH,
    CUSTOMER_CREATED,
    CUSTOMER_UPDATED,

    //Account
    ACCOUNT_CREATED,
    ACCOUNT_DEPOSIT,
    ACCOUNT_WITHDRAW,
    ACCOUNT_FETCH,
    ACCOUNT_BALANCE,

    //Ledger
    LEDGER_CREATED,
    LEDGER_FETCH,

    //Transfer
    TRANSFER,
    TRANSFER_FETCH
}
