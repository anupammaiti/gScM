package com.domain.mysql.dialect;

import org.hibernate.dialect.MySQLInnoDBDialect;

/**
 * Created by aminul on 1/8/2015.
 */
public class MySQLUTF8InnoDBDialect extends MySQLInnoDBDialect {


    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}
