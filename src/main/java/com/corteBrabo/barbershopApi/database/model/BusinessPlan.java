package com.corteBrabo.barbershopApi.database.model;

import java.math.BigDecimal;

public enum BusinessPlan {
    ESSENCIAL(new BigDecimal("97.00"), 1, false),
    PROFISSIONAL(new BigDecimal("197.00"), 5, true),
    PREMIUM(new BigDecimal("297.00"), Integer.MAX_VALUE, true);

    private final BigDecimal monthlyPrice;
    private final int maxProfessionals;
    private final boolean membershipEnabled;

    BusinessPlan(BigDecimal monthlyPrice, int maxProfessionals, boolean membershipEnabled) {
        this.monthlyPrice = monthlyPrice;
        this.maxProfessionals = maxProfessionals;
        this.membershipEnabled = membershipEnabled;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public int getMaxProfessionals() {
        return maxProfessionals;
    }

    public boolean isMembershipEnabled() {
        return membershipEnabled;
    }
}
