package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import com.aswinayyappadas.usingDatastrutures.user.UserData;
/**
 * Provides methods for checking the validity of user IDs, employer IDs, job seeker IDs, and job IDs.
 */
public class ValidityCheckingService implements UserData, JobListData {


    /**
     * Checks if a user ID is valid.
     *
     * @param userId The user ID to check.
     * @return {@code true} if the user ID is valid, {@code false} otherwise.
     */
    public boolean isValidUserId(int userId) {
        return userData.containsKey(userId);
    }

    /**
     * Checks if an employer ID is valid.
     *
     * @param employerId The employer ID to check.
     * @return {@code true} if the employer ID is valid, {@code false} otherwise.
     */
    public boolean isValidEmployerId(int employerId) {
        // Check if the user data contains the ID and the corresponding user is an instance of Employer
        return userData.containsKey(employerId) && userData.get(employerId) instanceof Employer;
    }

    /**
     * Checks if a job seeker ID is valid.
     *
     * @param jobSeekerId The job seeker ID to check.
     * @return {@code true} if the job seeker ID is valid, {@code false} otherwise.
     */
    public boolean isValidJobSeekerId(int jobSeekerId) {
        // Check if the user data contains the ID and the corresponding user is an instance of Jobseeker
        return userData.containsKey(jobSeekerId) && userData.get(jobSeekerId) instanceof Jobseeker;
    }

    /**
     * Checks if a job ID is valid.
     *
     * @param jobId The job ID to check.
     * @return {@code true} if the job ID is valid, {@code false} otherwise.
     */
    public boolean  isValidJobId(int jobId) {
        return jobList.containsKey(jobId);
    }
}
