package com.example.demo.security;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SecurityConstants {
    public static String HEADER_STRING = "Authorization";
    public static String SECRET = "udacitySecret";
    public static String TOKEN_PREFIX = "Bearer ";
    public static String SIGN_UP_URL = "/api/user/create";

    public static Date getExpirationTime() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, 10);
        return cal.getTime();
    }
}
