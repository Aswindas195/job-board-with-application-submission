package com.aswinayyappadas.usingDatastrutures.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.HashSet;
import org.json.JSONObject;
import com.aswinayyappadas.usingDatabase.exceptions.ExceptionHandler;
import com.aswinayyappadas.usingDatastrutures.applications.Application;

public class ApplicationServiceTest {

    private ApplicationService applicationService;

    @BeforeEach
    public void setUp() {
        applicationService = new ApplicationService();
    }


    @Test
    public void testHasUserAppliedForJob() {
        assertFalse(!applicationService.hasUserAppliedForJob(1, 1));

        // Manually add an application to simulate an applied job
        applicationService.jobseekerApplicationList.put(1, new HashSet<>());
        applicationService.jobseekerApplicationList.get(1).add(1);

        assertTrue(applicationService.hasUserAppliedForJob(1, 1));
        assertFalse(applicationService.hasUserAppliedForJob(2, 1));
    }

    @Test
    public void testApplyForJob_UserAlreadyApplied() {
        // Manually add an application to simulate an applied job
        applicationService.jobseekerApplicationList.put(1, new HashSet<>());
        applicationService.jobseekerApplicationList.get(1).add(1);

        assertThrows(ExceptionHandler.class, () -> applicationService.applyForJob(1, 1));
    }

    @Test
    public void testApplyForJob_SuccessfulApplication() throws ExceptionHandler {
        JSONObject applicationJson = applicationService.applyForJob(1, 2);

        assertNotNull(applicationJson);
        assertEquals(1, applicationJson.getInt("jobseekerId"));
        assertEquals(2, applicationJson.getInt("jobId"));

        assertTrue(applicationService.jobApplicationsList.containsKey(2));
        assertTrue(applicationService.jobApplicationsList.get(2).contains(applicationJson.getInt("applicationId")));
        assertTrue(applicationService.jobseekerApplicationList.containsKey(1));
        assertTrue(applicationService.jobseekerApplicationList.get(1).contains(2));
    }

    @Test
    public void testGetValidApplicationId() {
        int applicationId = applicationService.getValidApplicationId();
        assertTrue(!applicationService.checkApplicationIdValidity.isValidApplicationId(applicationId, applicationService.applicationList));
    }

    @Test
    public void testDeleteJobApplicationByJobSeekerId() {
        // Manually add an application to simulate an applied job
        applicationService.jobseekerApplicationList.put(1, new HashSet<>());
        applicationService.jobseekerApplicationList.get(1).add(1);

        applicationService.deleteJobApplicationByJobSeekerId(1, 1);
        assertFalse(applicationService.jobseekerApplicationList.get(1).contains(1));
    }

    @Test
    public void testUpdateResumeFilePath() {
        // Manually add an application to simulate an applied job
        Application application = new Application();
        application.setApplicationId(1);
        application.setJobId(1);
        application.setJobseekerId(1);
        application.setSubmissionDate(new Date());
        application.setCoverLetter("OldCoverLetter");
        application.setResumeFilePath("OldResumePath");

        applicationService.applicationList.put(1, application);

        String newResumePath = "NewResumePath";
        applicationService.updateResumeFilePath(1, 1, newResumePath);

        assertEquals(newResumePath, applicationService.applicationList.get(1).getResumeFilePath());
    }

    @Test
    public void testUpdateCoverLetter() {
        // Manually add an application to simulate an applied job
        Application application = new Application();
        application.setApplicationId(1);
        application.setJobId(1);
        application.setJobseekerId(1);
        application.setSubmissionDate(new Date());
        application.setCoverLetter("OldCoverLetter");
        application.setResumeFilePath("OldResumePath");

        applicationService.applicationList.put(1, application);

        String newCoverLetter = "NewCoverLetter";
        applicationService.updateCoverLetter(1, 1, newCoverLetter);

        assertEquals(newCoverLetter, applicationService.applicationList.get(1).getCoverLetter());
    }

    @Test
    public void testDisplayUpdatedApplication() {
        // Manually add an application to simulate an applied job
        Application application = new Application();
        application.setApplicationId(1);
        application.setJobId(1);
        application.setJobseekerId(1);
        application.setSubmissionDate(new Date());
        application.setCoverLetter("OldCoverLetter");
        application.setResumeFilePath("OldResumePath");

        applicationService.applicationList.put(1, application);

        JSONObject updatedApplicationJson = applicationService.displayUpdatedApplication(1, 1);

        assertNotNull(updatedApplicationJson);
        assertEquals(1, updatedApplicationJson.getInt("jobseekerId"));
        assertEquals(1, updatedApplicationJson.getInt("jobId"));
        assertEquals("OldCoverLetter", updatedApplicationJson.getString("coverLetter"));
        assertEquals("OldResumePath", updatedApplicationJson.getString("resumeFilePath"));
    }


}
