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

   /**
    * Post a new job with the specified details.
    *
    * @param employerId     The ID of the employer posting the job.
    * @param industry       The industry code of the job.
    * @param jobType        The job type code of the job.
    * @param jobTitle       The title of the job.
    * @param jobDescription The description of the job.
    * @param requirements   The requirements for the job.
    * @param location       The location code of the job.
    * @return The ID of the newly posted job.
    */
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

   /**
    * Get a valid job ID.
    *
    * @return A valid job ID.
    */
   public int getValidJobId() {
      int jobId = -1;
      while (true) {
         jobId = jobIdGenerator.generateRandomUserId();
         if (!checkJobIdValidity.isValidJobId(jobId, jobList)) break;
      }
      return jobId;
   }
   /**
    * Delete a job post.
    *
    * @param employerId The ID of the employer attempting to delete the job.
    * @param jobId      The ID of the job to be deleted.
    * @throws ExceptionHandler If the job is not found or not authorized to delete.
    */
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

   /**
    * Update job description.
    *
    * @param jobId             The ID of the job to update.
    * @param newJobDescription The new description for the job.
    * @return The updated job description.
    */
   public String updateJobDescription(int jobId, String newJobDescription) {
      jobList.get(jobId).setDescription(newJobDescription);
      return newJobDescription;
   }

   /**
    * Update job location.
    *
    * @param jobId       The ID of the job to update.
    * @param newLocation The new location ID for the job.
    * @return The updated job location.
    */
   public String updateJobLocation(int jobId, int newLocation) {
      String strLocation = locationJobTypeIndustry.getLocationMap().containsKey(newLocation) ? locationJobTypeIndustry.getLocationMap().get(newLocation) : "Unknown";
      jobList.get(jobId).setLoaction(strLocation);
      return strLocation;
   }

   /**
    * Update job requirements.
    *
    * @param jobId           The ID of the job to update.
    * @param newRequirements The new requirements for the job.
    * @return The updated job requirements.
    */
   public String updateJobRequirements(int jobId, String newRequirements) {
      jobList.get(jobId).setRequirements(newRequirements);
      return newRequirements;
   }
   /**
    * Update job type.
    *
    * @param jobId       The ID of the job to update.
    * @param newJobType   The new job type ID for the job.
    * @return The updated job type.
    */
   public String updateJobType(int jobId, int newJobType) {
      String strJobType = locationJobTypeIndustry.getJobTypeMap().containsKey(newJobType) ? locationJobTypeIndustry.getJobTypeMap().get(newJobType) : "Unknown";
      jobList.get(jobId).setJobType(strJobType);
      return strJobType;
   }

   /**
    * Update industry.
    *
    * @param jobId       The ID of the job to update.
    * @param newIndustry The new industry ID for the job.
    * @return The updated industry.
    */
   public String updateIndustry(int jobId, int newIndustry) {
      String strIndustry = locationJobTypeIndustry.getIndustryMap().containsKey(newIndustry) ? locationJobTypeIndustry.getIndustryMap().get(newIndustry) : "Unknown";
      jobList.get(jobId).setIndustry(strIndustry);
      return strIndustry;
   }
}
