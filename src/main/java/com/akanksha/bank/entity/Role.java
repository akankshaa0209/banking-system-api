package com.akanksha.bank.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    @JsonCreator
    public static Role from(String value) {
        return Role.valueOf("ROLE_" + value.toUpperCase());
    }
}