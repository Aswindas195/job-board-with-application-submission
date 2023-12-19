package com.aswinayyappadas.usingDatastrutures.util.application;

import com.aswinayyappadas.usingDatastrutures.applications.Application;
import java.util.HashMap;

/**
 * A utility class for checking the validity of application IDs.
 */
public class CheckApplicationIdValidity {
    /**
     * Checks if an application ID is valid by verifying its presence in the given application list.
     *
     * @param applicationId The application ID to be checked for validity.
     * @param applicationList The HashMap containing application IDs and corresponding applications.
     * @return {@code true} if the application ID is valid, {@code false} otherwise.
     */
    public boolean isValidApplicationId(int applicationId, HashMap<Integer, Application> applicationList) {
        return applicationList.containsKey(applicationId);
    }
}
