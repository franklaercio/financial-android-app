package com.github.finacial.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String formatDate(Date localDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(localDate);
    }

    public static String showCurrentDate() {
        Date dataAtual = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));

        return formato.format(dataAtual);
    }
}
