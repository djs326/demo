package com.example.common.utils;

import com.example.common.enums.MaskType;

public class MaskUtils {
    public static String mask(String value, MaskType maskType) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        switch (maskType) {
            case PHONE:
                return maskPhone(value);
            case EMAIL:
                return maskEmail(value);
            case ID_CARD:
                return maskIdCard(value);
            case BANK_CARD:
                return maskBankCard(value);
            default:
                return maskDefault(value);
        }
    }

    private static String maskPhone(String phone) {
        if (phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return email;
        }
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

    private static String maskIdCard(String idCard) {
        if (idCard.length() < 15) {
            return idCard;
        }
        return idCard.substring(0, 6) + "****" + idCard.substring(idCard.length() - 4);
    }

    private static String maskBankCard(String bankCard) {
        if (bankCard.length() < 10) {
            return bankCard;
        }
        return bankCard.substring(0, 4) + "****" + bankCard.substring(bankCard.length() - 4);
    }

    private static String maskDefault(String value) {
        if (value.length() <= 4) {
            return "****";
        }
        int length = value.length();
        int maskLength = Math.min(length / 2, 8);
        int start = (length - maskLength) / 2;
        StringBuilder sb = new StringBuilder(value);
        for (int i = start; i < start + maskLength; i++) {
            sb.setCharAt(i, '*');
        }
        return sb.toString();
    }
}