package com.scottlogic.deg.generator.cucumber.engine.steps;

import java.util.HashMap;

public class DateObject extends HashMap {
    public DateObject(String date){
        this.put("date", date);
    }
}