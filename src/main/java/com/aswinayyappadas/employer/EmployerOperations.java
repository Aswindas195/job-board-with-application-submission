package com.aswinayyappadas.employer;

import com.aswinayyappadas.job.Job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EmployerOperations {
    private List<Job> jobList;

    public EmployerOperations() {
        this.jobList = new ArrayList<>();
    }

    // Add a job to the job list
    public void addJob(Job job) {
        jobList.add(job);
    }

    // Remove a job from the job list based on job ID
    public void removeJob(int jobId) {
        Iterator<Job> iterator = jobList.iterator();
        while (iterator.hasNext()) {
            Job job = iterator.next();
            if (job.getJobId() == jobId) {
                iterator.remove();
                break;
            }
        }
    }

    // Edit job details based on job ID
    public void editJob(int jobId, String newTitle, String newDescription, String newLocation, String newRequirements) {
        for (Job job : jobList) {
            if (job.getJobId() == jobId) {
                job.setTitle(newTitle);
                job.setDescription(newDescription);
                job.setLocation(newLocation);
                job.setRequirements(newRequirements);
                break;
            }
        }
    }

    // Get the list of jobs
    public List<Job> getJobList() {
        return jobList;
    }
}
