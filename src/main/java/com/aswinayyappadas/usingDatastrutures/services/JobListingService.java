package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList;
import com.aswinayyappadas.usingDatastrutures.job.Job;
import com.aswinayyappadas.usingDatastrutures.job.LocationJobTypeIndustry;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;
import com.aswinayyappadas.usingDatastrutures.util.job.CheckJobIdValidity;
import com.aswinayyappadas.usingDatastrutures.util.job.JobIdGenerator;
import com.aswinayyappadas.usingDatastrutures.util.user.UserIdGenerator;

import java.util.HashSet;

public class JobListingService implements JobListData, ApplicationsDataList {

   // Service to check the validity of job IDs
   private CheckJobIdValidity checkJobIdValidity;
   private LocationJobTypeIndustry locationJobTypeIndustry;
   // Service to generate unique job IDs
   private JobIdGenerator jobIdGenerator;

   // Constructor to initialize services
   public JobListingService() {
      this.checkJobIdValidity = new CheckJobIdValidity();
      this.jobIdGenerator = new JobIdGenerator();
      this.locationJobTypeIndustry = new LocationJobTypeIndustry();
   }

   // Method to post a new job
   public int postJob(int employerId, int industry, int jobType, String jobTitle, String jobDescription, String requirements, int location) {
      // Create new job
      Job job = new Job();
      job.setJobId(getValidJobId());
      job.setTitle(jobTitle);
      job.setDescription(jobDescription);
      String strLocation = locationJobTypeIndustry.getLocationMap().containsKey(location) ? locationJobTypeIndustry.getLocationMap().get(location) : "Unknown";
      job.setLoaction(strLocation);
      String strJobType = locationJobTypeIndustry.getJobTypeMap().containsKey(jobType) ? locationJobTypeIndustry.getJobTypeMap().get(jobType) : "Unknown";
      job.setJobType(strJobType);
      String strIndustry = locationJobTypeIndustry.getIndustryMap().containsKey(industry) ? locationJobTypeIndustry.getIndustryMap().get(industry) : "Unknown";
      job.setIndustry(strIndustry);
      job.setRequirements(requirements);

      // Put details in the job list
      jobList.put(job.getJobId(), job);

      // Add details into employer job list
      employerJobList.putIfAbsent(employerId, new HashSet<Integer>());
      employerJobList.get(employerId).add(job.getJobId());

      return job.getJobId();
   }

   // Method to get a valid job ID
   public int getValidJobId() {
      int jobId = -1;
      while (true) {
         jobId = jobIdGenerator.generateRandomUserId();
         if (!checkJobIdValidity.isValidJobId(jobId, jobList)) break;
      }
      return jobId;
   }

   // Method to delete a job post
   public void deleteJobPost(int employerId, int jobId) throws ExceptionHandler {
      try {
         // Check if the job exists and the employer is authorized to delete it
         if (jobList.containsKey(jobId) && employerJobList.containsKey(employerId)
                 && employerJobList.get(employerId).contains(jobId)) {
            // Remove the job from job list
            jobList.remove(jobId);

            // Remove the job from the employer's job list
            employerJobList.get(employerId).remove(jobId);

            // Remove the job from job applications of all job seekers
            for (int jobseekerId : jobseekerApplicationList.keySet()) {
               if (jobseekerApplicationList.get(jobseekerId).contains(jobId)) {
                  jobseekerApplicationList.get(jobseekerId).remove(jobId);
               }
            }
         } else {
            // Throw an exception if the job is not found or not authorized to delete
            throw new ExceptionHandler("Job not found or not authorized to delete the job.");
         }
      } catch (Exception e) {
         // Catching a general Exception might be too broad, consider catching a more specific exception if possible.
         throw new ExceptionHandler(e.getMessage(), e);
      }
   }

   // Method to update job description
   public String updateJobDescription(int jobId, String newJobDescription) {
      jobList.get(jobId).setDescription(newJobDescription);
      return newJobDescription;
   }

   // Method to update job location
   public String updateJobLocation(int jobId, int newLocation) {
      String strLocation = locationJobTypeIndustry.getLocationMap().containsKey(newLocation) ? locationJobTypeIndustry.getLocationMap().get(newLocation) : "Unknown";
      jobList.get(jobId).setLoaction(strLocation);
      return strLocation;
   }

   // Method to update job requirements
   public String updateJobRequirements(int jobId, String newRequirements) {
      jobList.get(jobId).setRequirements(newRequirements);
      return newRequirements;
   }

   public String updateJobType(int jobId, int newJobType) {
      String strJobType = locationJobTypeIndustry.getJobTypeMap().containsKey(newJobType) ? locationJobTypeIndustry.getJobTypeMap().get(newJobType) : "Unknown";
      jobList.get(jobId).setJobType(strJobType);
      return strJobType;
   }

   public String updateIndustry(int jobId, int newIndustry) {
      String strIndustry = locationJobTypeIndustry.getIndustryMap().containsKey(newIndustry) ? locationJobTypeIndustry.getIndustryMap().get(newIndustry) : "Unknown";
      jobList.get(jobId).setIndustry(strIndustry);
      return strIndustry;
   }
}
