package com.aswinayyappadas.usingDatastrutures.joblistings;

import com.aswinayyappadas.usingDatastrutures.job.Job;

import java.util.HashMap;
import java.util.HashSet;
/**
 * A data interface for managing job listings.
 */
public interface JobListData {
    /**
     * A map containing job listings with job IDs as keys and corresponding Job objects as values.
     */
    HashMap<Integer, Job> jobList = new HashMap<>();
    /**
     * A map containing employer job listings with employer IDs as keys and sets of job IDs as values.
     */
    HashMap<Integer, HashSet<Integer>> employerJobList = new HashMap<>();
}
