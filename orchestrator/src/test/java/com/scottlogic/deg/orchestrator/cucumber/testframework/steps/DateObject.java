package com.scottlogic.deg.orchestrator.cucumber.testframework.steps;

import java.util.HashMap;

public class DateObject extends HashMap {
    public DateObject(String date){
        this.put("date", date);
    }
}
