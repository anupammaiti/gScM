package com.grailslab.enums

/**
 * Created by khalil.ullah on 10/15/14.
 */
public enum BankAccount {

    PUBALI_BANK("Pubali Bank"),
    DBBL_BANK("DBBL Bank")

    final String value

    BankAccount(String value) {
        this.value = value
    }
    String toString() { value }
    String getKey() { name() }

}