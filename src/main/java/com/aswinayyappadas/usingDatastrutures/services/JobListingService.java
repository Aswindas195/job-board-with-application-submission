package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatastrutures.job.Job;
import com.aswinayyappadas.usingDatastrutures.joblistings.JobListData;
import com.aswinayyappadas.usingDatastrutures.util.job.CheckJobIdValidity;
import com.aswinayyappadas.usingDatastrutures.util.job.JobIdGenerator;
import com.aswinayyappadas.usingDatastrutures.util.user.UserIdGenerator;

public class JobListingService implements JobListData {
   private CheckJobIdValidity checkJobIdValidity;
   private JobIdGenerator jobIdGenerator;

   public JobListingService() {
      this.checkJobIdValidity = new CheckJobIdValidity();
      this.jobIdGenerator = new JobIdGenerator();
   }
   public int postJob(int employerId, String jobTitle, String jobDescription, String requirements, String location) {
      // Create new job
      Job job = new Job();
      job.setJobId(getValidJobId());
      job.setTitle(jobTitle);
      job.setDescription(jobDescription);
      job.setLoaction(location);
      job.setRequirements(requirements);
      // Put details in employer job list also
      employerJobList.put(employerId, job);
      return job.getJobId();
   }
   public int getValidJobId() {
      int jobId = -1;
      while(true) {
         jobId = jobIdGenerator.generateRandomUserId();
         if(!checkJobIdValidity.isValidJobId(jobId, jobList)) break;
      }
      return jobId;
   }

   public void deleteJobPost(int employerId, int jobId) throws ExceptionHandler {
      try {
         if (jobList.containsKey(jobId) && employerJobList.containsKey(employerId)
                 && employerJobList.get(employerId).getJobId() == jobId) {
            jobList.remove(jobId);
            employerJobList.remove(employerId);
         } else {
            throw new ExceptionHandler("Job not found or not authorized to delete the job.");
         }
      } catch (Exception e) {
         // Catching a general Exception might be too broad, consider catching a more specific exception if possible.
         throw new ExceptionHandler("Error deleting job post: " + e.getMessage(), e);
      }
   }
}
