package com.aswinayyappadas.usingDatastrutures.applications;

import java.util.HashMap;
import java.util.HashSet;
/**
 * Interface defining data structures for managing job applications.
 */
public interface ApplicationsDataList {
    /**
     * A map associating application IDs with their corresponding applications.
     */
    HashMap<Integer, Application> applicationList = new HashMap<>();
    /**
     * A map associating job seeker IDs with sets of application IDs they have submitted.
     */
    HashMap<Integer, HashSet<Integer>> jobseekerApplicationList = new HashMap<>();
    /**
     * A map associating job IDs with sets of application IDs received for those jobs.
     */
    HashMap<Integer, HashSet<Integer>> jobApplicationsList = new HashMap<>();
}
