package com.aswinayyappadas.usingDatastrutures.util.application;

import com.aswinayyappadas.usingDatastrutures.applications.Application;
import java.util.HashMap;


public class CheckApplicationIdValidity {
    public boolean isValidApplicationId(int applicationId, HashMap<Integer, Application> applicationList) {
        return applicationList.containsKey(applicationId);
    }
}
