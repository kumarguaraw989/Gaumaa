package com.ecom.agrisewa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constant {

    public static final String BASE_URL = "https://gaumaa.tripledotss.com/api/";
    public static final String PAYMENT_URL = "https://api.razorpay.com/v1/";

    public static String getFormattedDate(String date) {
        //yyyy-MM-dd
        String day = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date2 = formatter.parse(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
            day = dateFormat.format(date2);
            System.out.println("New Converted Date: " + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

}
