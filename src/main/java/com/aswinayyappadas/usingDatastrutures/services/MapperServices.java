package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;

public class MapperServices implements JobListData, ApplicationsDataList {

    // Method to check if a job is mapped to an employer
    public boolean isJobMappedToEmployer(int jobId, int employerId) {
        // Check if the job exists, the employer exists, and the job is mapped to the employer
        if (jobList.containsKey(jobId) && employerJobList.containsKey(employerId)
                && employerJobList.get(employerId).contains(jobId)) {
            return true; // Job is mapped to the employer
        }
        return false; // Job is not mapped to the employer
    }

    // Method to check if an application is mapped to a job seeker
    public boolean isApplicationMappedToJobSeeker(int jobSeekerId, int jobId) {
        // Check if the job seeker's applications list contains the specified job ID
        if (jobseekerApplicationList.containsKey(jobSeekerId) && jobseekerApplicationList.get(jobSeekerId).contains(jobId)) {
            return true; // Application is mapped to the job seeker
        }
        return false; // Application is not mapped to the job seeker
    }

    public boolean isEmployerMappedToJob(int employerId, int jobId) {
        // Check if the employer exists, the job exists, and the employer has posted the job
        if (employerJobList.containsKey(employerId) && jobList.containsKey(jobId)
                && employerJobList.get(employerId).contains(jobId)) {
            return true; // Employer is mapped to the job
        }
        return false; // Employer is not mapped to the job
    }

}
