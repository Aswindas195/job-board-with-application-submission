package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList;
import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.job.Job;
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
        return "Job Seeker";
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
                    .put("location", jobList.get(jobId).getLoaction())
                    .put("jobType", jobList.get(jobId).getJobType())
                    .put("industry", jobList.get(jobId).getIndustry());
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
                        .put("location", jobList.get(jobId).getLoaction())
                        .put("jobType", jobList.get(jobId).getJobType())
                        .put("industry", jobList.get(jobId).getIndustry());
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
                        .put("location", jobList.get(jobId).getLoaction())
                        .put("jobType", jobList.get(jobId).getJobType())
                        .put("industry", jobList.get(jobId).getIndustry());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    // Get all jobs from listings
    public JSONArray getAllJobsFromListings() {
        JSONArray jobPosts = new JSONArray();
        for (Job job : jobList.values()) {
            JSONObject jobPost = new JSONObject()
                    .put("jobId", job.getJobId())
                    .put("title", job.getTitle())
                    .put("description", job.getDescription())
                    .put("requirements", job.getRequirements())
                    .put("location", job.getLoaction())
                    .put("jobType", job.getJobType())
                    .put("industry", job.getIndustry());
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
                        .put("location", jobList.get(jobId).getLoaction())
                        .put("jobType", jobList.get(jobId).getJobType())
                        .put("industry", jobList.get(jobId).getIndustry());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    public JSONArray getApplicationsByJob(int employerId, int jobId) {
        JSONArray applicationsArray = new JSONArray();

        if (employerJobList.containsKey(employerId) && employerJobList.get(employerId).contains(jobId)
                && jobApplicationsList.containsKey(jobId)) {
            for(int applicationId : jobApplicationsList.get(jobId)) {
                JSONObject applicationObject = new JSONObject();
                applicationObject.put("applicationId", applicationId);

                if (applicationList.containsKey(applicationId)) {
                    applicationObject.put("jobSeekerId", applicationList.get(applicationId).getJobseekerId());
                    applicationObject.put("submissionDate", applicationList.get(applicationId).getSubmissionDate());
                    applicationObject.put("coverLetter", applicationList.get(applicationId).getCoverLetter());
                    applicationObject.put("resumeFilePath", applicationList.get(applicationId).getResumeFilePath());

                    int jobSeekerId = applicationList.get(applicationId).getJobseekerId();

                    // Assuming you have additional information in the user data structure
                    if (userData.containsKey(jobSeekerId)) {
                        applicationObject.put("jobSeekerUsername", userData.get(jobSeekerId).getName());
                        applicationObject.put("jobSeekerEmail", userData.get(jobSeekerId).getEmail());
                    }
                }
                applicationsArray.put(applicationObject);
            }
            }

        return applicationsArray;
    }

    public JSONArray getJobsByLocationIndustryType(String location, String industry, String jobType) {
        JSONArray jobPosts = new JSONArray();
        for(Job job : jobList.values()) {
            if(job.getLoaction().equals(location) && job.getIndustry().equals(industry) && job.getJobType().equals(jobType)) {
                int jobId = job.getJobId();
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobId)
                        .put("title", job.getTitle())
                        .put("description", job.getDescription())
                        .put("requirements", job.getRequirements())
                        .put("location", job.getLoaction())
                        .put("jobType", job.getJobType())
                        .put("industry", job.getIndustry());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    public JSONArray getJobsByLocationIndustry(String location, String industry) {
        JSONArray jobPosts = new JSONArray();
        for (Job job : jobList.values()) {
            if (job.getLoaction().equals(location) && job.getIndustry().equals(industry)) {
                int jobId = job.getJobId();
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobId)
                        .put("title", job.getTitle())
                        .put("description", job.getDescription())
                        .put("requirements", job.getRequirements())
                        .put("location", job.getLoaction())
                        .put("jobType", job.getJobType())
                        .put("industry", job.getIndustry());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    public JSONArray getJobsByLocationType(String location, String jobType) {
        JSONArray jobPosts = new JSONArray();
        for (Job job : jobList.values()) {
            if (job.getLoaction().equals(location) && job.getJobType().equals(jobType)) {
                int jobId = job.getJobId();
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobId)
                        .put("title", job.getTitle())
                        .put("description", job.getDescription())
                        .put("requirements", job.getRequirements())
                        .put("location", job.getLoaction())
                        .put("jobType", job.getJobType())
                        .put("industry", job.getIndustry());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    public JSONArray getJobsByIndustryType(String industry, String jobType) {
        JSONArray jobPosts = new JSONArray();
        for (Job job : jobList.values()) {
            if (job.getIndustry().equals(industry) && job.getJobType().equals(jobType)) {
                int jobId = job.getJobId();
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobId)
                        .put("title", job.getTitle())
                        .put("description", job.getDescription())
                        .put("requirements", job.getRequirements())
                        .put("location", job.getLoaction())
                        .put("jobType", job.getJobType())
                        .put("industry", job.getIndustry());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    public JSONArray getJobsByIndustry(String industry) {
        JSONArray jobPosts = new JSONArray();
        for (Job job : jobList.values()) {
            if (job.getIndustry().equals(industry)) {
                int jobId = job.getJobId();
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobId)
                        .put("title", job.getTitle())
                        .put("description", job.getDescription())
                        .put("requirements", job.getRequirements())
                        .put("location", job.getLoaction())
                        .put("jobType", job.getJobType())
                        .put("industry", job.getIndustry());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    public JSONArray getJobsByType(String jobType) {
        JSONArray jobPosts = new JSONArray();
        for (Job job : jobList.values()) {
            if (job.getJobType().equals(jobType)) {
                int jobId = job.getJobId();
                JSONObject jobPost = new JSONObject()
                        .put("jobId", jobId)
                        .put("title", job.getTitle())
                        .put("description", job.getDescription())
                        .put("requirements", job.getRequirements())
                        .put("location", job.getLoaction())
                        .put("jobType", job.getJobType())
                        .put("industry", job.getIndustry());
                jobPosts.put(jobPost);
            }
        }
        return jobPosts;
    }

    public JSONObject getJobPostsByEmployer(int userId, int jobId) {
        // Check if the employer has posted the specified job
        if (employerJobList.containsKey(userId) && employerJobList.get(userId).contains(jobId)
                && jobList.containsKey(jobId)) {

            Job job = jobList.get(jobId);

            // Construct a JSON object for the job post
            JSONObject jobPost = new JSONObject()
                    .put("jobId", job.getJobId())
                    .put("title", job.getTitle())
                    .put("description", job.getDescription())
                    .put("requirements", job.getRequirements())
                    .put("location", job.getLoaction())
                    .put("jobType", job.getJobType())
                    .put("industry", job.getIndustry());

            return jobPost;
        }

        // If the specified job is not found or the user is not an employer, return an empty JSONObject
        return null;
    }
}
