package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList;
import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;
import com.aswinayyappadas.usingDatastrutures.user.UserData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class GetServices implements UserData, JobListData, ApplicationsDataList {

    // Get user type (Employer or Jobseeker) based on user ID
    public String getUserTypeByUserId(int userId) {
        if (userData.get(userId) instanceof Employer) {
            return "Employer";
        }
        return "Jobseeker";
    }

    // Get email by user ID
    public String getEmailByUserId(int userId) {
        return userData.get(userId).getEmail();
    }

    // Get job posts by employer ID
    public JSONArray getJobPostsByEmployer(int employerId) {
        JSONArray jobPosts = new JSONArray();
        for (int jobId : employerJobList.get(employerId)) {
            // Construct a JSON object for each job post
            JSONObject jobPost = new JSONObject()
                    .put("jobId", jobList.get(jobId).getJobId())
                    .put("title", jobList.get(jobId).getTitle())
                    .put("description", jobList.get(jobId).getDescription())
                    .put("requirements", jobList.get(jobId).getRequirements())
                    .put("location", jobList.get(jobId).getLoaction());
            jobPosts.put(jobPost);
        }
        return jobPosts;
    }

    // Get jobs by location
    public JSONArray getJobsByLocation(String location) {
        JSONArray jobPosts = new JSONArray();
        for (int jobId : jobList.keySet()) {
            if (jobList.get(jobId).getLoaction().equals(location)) {
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobList.get(jobId).getJobId())
                        .put("title", jobList.get(jobId).getTitle())
                        .put("description", jobList.get(jobId).getDescription())
                        .put("requirements", jobList.get(jobId).getRequirements())
                        .put("location", jobList.get(jobId).getLoaction());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    // Get jobs by title
    public JSONArray getJobsByTitle(String title) {
        JSONArray jobPosts = new JSONArray();
        for (int jobId : jobList.keySet()) {
            if (jobList.get(jobId).getTitle().equals(title)) {
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobList.get(jobId).getJobId())
                        .put("title", jobList.get(jobId).getTitle())
                        .put("description", jobList.get(jobId).getDescription())
                        .put("requirements", jobList.get(jobId).getRequirements())
                        .put("location", jobList.get(jobId).getLoaction());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    // Get all jobs from listings
    public JSONArray getAllJobsFromListings() {
        JSONArray jobPosts = new JSONArray();
        for (int jobId : jobList.keySet()) {
            JSONObject jobPost = new JSONObject()
                    .put("jobId", jobList.get(jobId).getJobId())
                    .put("title", jobList.get(jobId).getTitle())
                    .put("description", jobList.get(jobId).getDescription())
                    .put("requirements", jobList.get(jobId).getRequirements())
                    .put("location", jobList.get(jobId).getLoaction());
            jobPosts.put(jobPost);
        }
        return jobPosts;
    }

    // Get applied jobs by job seeker
    public JSONArray getAppliedJobsByJobSeeker(int jobSeekerId) {
        JSONArray jobPosts = new JSONArray();
        if (!jobseekerApplicationList.containsKey(jobSeekerId)) {
            return jobPosts;
        } else {
            for (int jobId : jobseekerApplicationList.get(jobSeekerId)) {
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobList.get(jobId).getJobId())
                        .put("title", jobList.get(jobId).getTitle())
                        .put("description", jobList.get(jobId).getDescription())
                        .put("requirements", jobList.get(jobId).getRequirements())
                        .put("location", jobList.get(jobId).getLoaction());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }
}
