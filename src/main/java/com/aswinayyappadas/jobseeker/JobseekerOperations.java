package com.aswinayyappadas.jobseeker;

import com.aswinayyappadas.application.Application;
import com.aswinayyappadas.job.Job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JobseekerOperations {
    private List<Application> applications;

    public JobseekerOperations() {
        this.applications = new ArrayList<>();
    }

    public void applyForJob(Job job, int jobseekerId, String resumeFilePath, String coverLetter) {
        Application application = new Application(
                jobseekerId, job.getJobId(), resumeFilePath, coverLetter, new Date()
        );
        applications.add(application);
        System.out.println("Applied for job: " + job.getTitle());
    }

    public void removeApplication(int applicationId) {
        applications.removeIf(application -> application.getApplicationId() == applicationId);
        System.out.println("Application removed successfully.");
    }

    public void viewAppliedJobs() {
        System.out.println("Applied Jobs:");
        for (Application application : applications) {
            System.out.println("Job ID: " + application.getJobId() + ", Application ID: " + application.getApplicationId());
        }
    }

    public void editApplication(int applicationId, String newResumeFilePath, String newCoverLetter) {
        for (Application application : applications) {
            if (application.getApplicationId() == applicationId) {
                application.setResumeFilePath(newResumeFilePath);
                application.setCoverLetter(newCoverLetter);
                System.out.println("Application edited successfully.");
                break;
            }
        }
    }
    // Advanced search for application list based on date
    public List<Application> searchAppliedJobsByDateRange(Date startDate, Date endDate) {
        return applications.stream()
                .filter(application -> isDateInRange(application.getSubmissionDate(), startDate, endDate))
                .collect(Collectors.toList());
    }

    private boolean isDateInRange(Date dateToCheck, Date startDate, Date endDate) {
        return !dateToCheck.before(startDate) && !dateToCheck.after(endDate);
    }


    // Advanced search for job list based on job title
    public List<Job> searchJobListByTitle(List<Job> jobs, String keyword) {
        return jobs.stream()
                .filter(job -> job.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
