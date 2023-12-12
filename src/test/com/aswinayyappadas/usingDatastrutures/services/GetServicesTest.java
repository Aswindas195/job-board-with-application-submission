package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.employer.Employer;
import com.aswinayyappadas.usingDatastrutures.job.Job;
import com.aswinayyappadas.usingDatastrutures.jobseeker.Jobseeker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;

import static com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList.applicationList;
import static com.aswinayyappadas.usingDatastrutures.applications.ApplicationsDataList.jobseekerApplicationList;
import static com.aswinayyappadas.usingDatastrutures.joblistings.JobListData.employerJobList;
import static com.aswinayyappadas.usingDatastrutures.joblistings.JobListData.jobList;
import static com.aswinayyappadas.usingDatastrutures.user.UserData.userData;
import static org.junit.jupiter.api.Assertions.*;

class GetServicesTest {

    private GetServices getServices;

    @BeforeEach
    void setUp() {
        // Initialize the GetServices instance before each test
        getServices = new GetServices();
        Employer employer = new Employer();
        Jobseeker jobseeker = new Jobseeker();
        employer.setEmail("employer@example.com");
        jobseeker.setEmail("jobseeker@example.com");

        HashSet<Integer> jobSet = new HashSet<>();
        HashSet<Integer> jobseekerJobSet = new HashSet<>();
        jobseekerJobSet.add(1);
        jobSet.add(1);
        Job job = new Job();
        job.setTitle("Software Engineer");
        job.setLoaction("City1");
        job.setIndustry("IT");
        job.setJobType("Full Time");
        jobseekerApplicationList.put(2, jobseekerJobSet);
        jobList.put(1, job);
        employerJobList.put(1, jobSet);
        userData.put(1, employer);
        userData.put(2, jobseeker);

        // Add any additional setup logic if needed
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
    void testGetUserTypeByUserId() {
        assertEquals("Employer", getServices.getUserTypeByUserId(1));
        assertEquals("Job Seeker", getServices.getUserTypeByUserId(2));
        // Add more test cases as needed
    }

    @Test
    void testGetEmailByUserId() {
        assertEquals("employer@example.com", getServices.getEmailByUserId(1));
        assertEquals("jobseeker@example.com", getServices.getEmailByUserId(2));
        // Add more test cases as needed
    }

    @Test
    void testGetJobPostsByEmployer() {
        int employerId = 1;
        assertEquals(1, getServices.getJobPostsByEmployer(employerId).length());
        // Add more assertions as needed
    }

    @Test
    void testGetJobsByLocation() {
        assertEquals(1, getServices.getJobsByLocation("City1").length());
        // Add more assertions as needed
    }

    @Test
    void testGetJobsByTitle() {
        assertEquals(1, getServices.getJobsByTitle("Software Engineer").length());
        // Add more assertions as needed
    }

    @Test
    void testGetAllJobsFromListings() {
        assertEquals(1, getServices.getAllJobsFromListings().length());
        // Add more assertions as needed
    }

    @Test
    void testGetAppliedJobsByJobSeeker() {
        assertEquals(1, getServices.getAppliedJobsByJobSeeker(2).length());
        // Add more assertions as needed
    }

    @Test
    void testGetApplicationsByJob() {
        assertEquals(0, getServices.getApplicationsByJob(1, 1).length());
        // Add more assertions as needed
    }

    @Test
    void testGetJobsByLocationIndustryType() {
        assertEquals(1, getServices.getJobsByLocationIndustryType("City1", "IT", "Full Time").length());
        // Add more assertions as needed
    }
    // Method to clear all data (reset data structures)
}