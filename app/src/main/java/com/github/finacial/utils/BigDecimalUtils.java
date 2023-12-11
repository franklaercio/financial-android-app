package com.github.finacial.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class BigDecimalUtils {

    public static String toBRCurrencyFormat(double value) {
        Locale brazil = new Locale("pt", "BR");
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(brazil);

        return currencyInstance.format(value);
    }
}
