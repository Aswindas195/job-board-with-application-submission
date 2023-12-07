package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import com.aswinayyappadas.usingDatastrutures.user.UserData;
import com.sun.source.tree.BreakTree;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetServices implements UserData, JobListData {
    public String getUserTypeByUserId(int userId) {
        if(userData.get(userId) instanceof Employer) {
            return "Employer";
        }
        return "Jobseeker";
    }
    public String getEmailByUserId(int userId) {
        return userData.get(userId).getEmail();
    }

    public JSONArray getJobPostsByEmployer(int employerId) {
        JSONArray jobPosts = new JSONArray();
        // Construct a JSON object for each job post
        JSONObject jobPost = new JSONObject()
                .put("jobId", employerJobList.get(employerId).getJobId())
                .put("title", employerJobList.get(employerId).getTitle())
                .put("description", employerJobList.get(employerId).getDescription())
                .put("requirements", employerJobList.get(employerId).getRequirements())
                .put("location", employerJobList.get(employerId).getLoaction());

        jobPosts.put(jobPost);
        return jobPosts;
    }
}
