package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import com.aswinayyappadas.usingDatastrutures.user.UserData;

public class ValidityCheckingService implements UserData, JobListData {

    // Method to check if a user ID is valid
    public boolean isValidUserId(int userId) {
        return userData.containsKey(userId);
    }

    // Method to check if an employer ID is valid
    public boolean isValidEmployerId(int employerId) {
        // Check if the user data contains the ID and the corresponding user is an instance of Employer
        return userData.containsKey(employerId) && userData.get(employerId) instanceof Employer;
    }

    // Method to check if a job seeker ID is valid
    public boolean isValidJobSeekerId(int jobSeekerId) {
        // Check if the user data contains the ID and the corresponding user is an instance of Jobseeker
        return userData.containsKey(jobSeekerId) && userData.get(jobSeekerId) instanceof Jobseeker;
    }

    // Method to check if a job ID is valid
    public boolean isValidJobId(int jobId) {
        return jobList.containsKey(jobId);
    }
}
