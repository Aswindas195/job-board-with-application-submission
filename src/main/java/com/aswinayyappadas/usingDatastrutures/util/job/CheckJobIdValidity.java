package com.aswinayyappadas.usingDatastrutures.util.job;

import com.aswinayyappadas.usingDatastrutures.job.Job;


import java.util.HashMap;
/**
 * A utility class for checking the validity of job IDs.
 */
public class CheckJobIdValidity {

    /**
     * Checks if a job ID is valid by verifying its presence in the given job list.
     *
     * @param jobId The job ID to be checked for validity.
     * @param jobList The HashMap containing job IDs and corresponding job details.
     * @return {@code true} if the job ID is valid, {@code false} otherwise.
     */
    public boolean isValidJobId(int jobId, HashMap<Integer, Job> jobList) {
        return jobList.containsKey(jobId);
    }
}
