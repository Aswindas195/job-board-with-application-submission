package com.aswinayyappadas.usingDatastrutures.services;

import com.aswinayyappadas.usingDatastrutures.job.Job;
import com.aswinayyappadas.usingDatastrutures.employer.Employer;
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

class MapperServicesTest {

    private MapperServices mapperServices;

    @BeforeEach
    void setUp() {
        // Initialize the MapperServices instance before each test
        mapperServices = new MapperServices();
        // Mock data
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
    void testIsJobMappedToEmployer() {
        assertTrue(mapperServices.isJobMappedToEmployer(1, 1));
        assertFalse(mapperServices.isJobMappedToEmployer(1, 2));
        assertFalse(mapperServices.isJobMappedToEmployer(2, 1));
    }

    @Test
    void testIsApplicationMappedToJobSeeker() {
        assertTrue(mapperServices.isApplicationMappedToJobSeeker(2, 1));
        assertFalse(mapperServices.isApplicationMappedToJobSeeker(1, 1));
        assertFalse(mapperServices.isApplicationMappedToJobSeeker(2, 2));
    }

    @Test
    void testIsEmployerMappedToJob() {
        assertTrue(mapperServices.isEmployerMappedToJob(1, 1));
        assertFalse(mapperServices.isEmployerMappedToJob(1, 2));
        assertFalse(mapperServices.isEmployerMappedToJob(2, 1));
    }
}
