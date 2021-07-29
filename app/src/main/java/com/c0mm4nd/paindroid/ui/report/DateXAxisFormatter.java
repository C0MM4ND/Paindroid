package com.c0mm4nd.paindroid.ui.report;

import com.c0mm4nd.paindroid.utils.DateUtil;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.Date;

public class DateXAxisFormatter extends IndexAxisValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        long timestamp = (long) value;

        Date date = new Date(timestamp * 1_000);
        return DateUtil.dateToString(date);
    }
}