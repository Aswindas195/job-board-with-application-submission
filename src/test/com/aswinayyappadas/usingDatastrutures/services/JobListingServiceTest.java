package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.job.Job;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList.applicationList;
import static com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList.jobseekerApplicationList;
import static com.aswinayyappadas.usingDatastrutures.joblistings.JobListData.employerJobList;
import static com.aswinayyappadas.usingDatastrutures.joblistings.JobListData.jobList;
import static com.aswinayyappadas.usingDatastrutures.user.UserData.userData;
import static org.junit.jupiter.api.Assertions.*;

public class JobListingServiceTest {

    private JobListingService jobListingService;

    @BeforeEach
    void setUp() {
        // Initialize the JobListingService instance before each test
        jobListingService = new JobListingService();

        // Mock data
        Employer employer = new Employer();
        Jobseeker jobseeker = new Jobseeker();
        employer.setEmail("employer@example.com");
        jobseeker.setEmail("jobseeker@example.com");
        HashSet<Integer> jobSet = new HashSet<>();
        jobSet.add(1);
        Job job = new Job();
        job.setTitle("Software Engineer");
        job.setLoaction("City1");
        job.setIndustry("IT");
        job.setJobType("Full Time");
        jobList.put(1, job);
        employerJobList.put(1, jobSet);
        userData.put(1, employer);
        userData.put(2, jobseeker);
    }
    @AfterEach
    void tearDown() {
        // Clear all data after each test
        userData.clear();
        jobList.clear();
        applicationList.clear();
        employerJobList.clear();
        jobseekerApplicationList.clear();
    }
    @Test
    void testPostJob() {
        int employerId = 1;
        int industry = 1;
        int jobType = 1;
        String jobTitle = "Software Engineer";
        String jobDescription = "Job Description";
        String requirements = "Requirements";
        int location = 1;

        jobListingService.postJob(employerId, industry, jobType, jobTitle, jobDescription, requirements, location);

        assertEquals(2, jobList.size());  // Assuming you have one job in the setup
        assertEquals(2, employerJobList.get(employerId).size());
    }

    @Test
    void testDeleteJobPost() {
        int employerId = 1;
        int jobId = 1;

        assertDoesNotThrow(() -> jobListingService.deleteJobPost(employerId, jobId));
        assertEquals(0, jobList.size());
    }

    @Test
    void testUpdateJobDescription() {
        int jobId = 1;
        String newJobDescription = "Updated Job Description";

        String updatedDescription = jobListingService.updateJobDescription(jobId, newJobDescription);

        assertEquals(newJobDescription, updatedDescription);
    }

    @Test
    void testUpdateJobLocation() {
        int jobId = 1;
        int newLocation = 2;

        String updatedLocation = jobListingService.updateJobLocation(jobId, newLocation);

        assertEquals("Mumbai", updatedLocation);  // Assuming "Unknown" is the default value
    }

    @Test
    void testUpdateJobRequirements() {
        int jobId = 1;
        String newRequirements = "Updated Requirements";

        String updatedRequirements = jobListingService.updateJobRequirements(jobId, newRequirements);

        assertEquals(newRequirements, updatedRequirements);
    }

    @Test
    void testUpdateJobType() {
        int jobId = 1;
        int newJobType = 2;

        String updatedJobType = jobListingService.updateJobType(jobId, newJobType);

        assertEquals("Part Time", updatedJobType);  // Assuming "Unknown" is the default value
    }

    @Test
    void testUpdateIndustry() {
        int jobId = 1;
        int newIndustry = 2;

        String updatedIndustry = jobListingService.updateIndustry(jobId, newIndustry);

        assertEquals("Finance", updatedIndustry);  // Assuming "Unknown" is the default value
    }
}
