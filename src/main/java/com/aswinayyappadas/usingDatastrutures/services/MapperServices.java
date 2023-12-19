package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;

public class MapperServices implements JobListData, ApplicationsDataList {
    /**
     * Check if a job is mapped to an employer.
     *
     * @param jobId      The ID of the job to check.
     * @param employerId The ID of the employer to check against.
     * @return {@code true} if the job is mapped to the employer, {@code false} otherwise.
     */
    public boolean isJobMappedToEmployer(int jobId, int employerId) {
        // Check if the job exists, the employer exists, and the job is mapped to the employer
        if (jobList.containsKey(jobId) && employerJobList.containsKey(employerId)
                && employerJobList.get(employerId).contains(jobId)) {
            return true; // Job is mapped to the employer
        }
        return false; // Job is not mapped to the employer
    }
    /**
     * Check if an application is mapped to a job seeker.
     *
     * @param jobSeekerId The ID of the job seeker to check against.
     * @param jobId       The ID of the job to check.
     * @return {@code true} if the application is mapped to the job seeker, {@code false} otherwise.
     */
    public boolean isApplicationMappedToJobSeeker(int jobSeekerId, int jobId) {
        // Check if the job seeker's applications list contains the specified job ID
        if (jobseekerApplicationList.containsKey(jobSeekerId) && jobseekerApplicationList.get(jobSeekerId).contains(jobId)) {
            return true; // Application is mapped to the job seeker
        }
        return false; // Application is not mapped to the job seeker
    }
}
