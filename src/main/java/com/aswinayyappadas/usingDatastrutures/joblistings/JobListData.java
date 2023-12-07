package com.aswinayyappadas.usingDatastrutures.joblistings;

import com.aswinayyappadas.usingDatastrutures.job.Job;

import java.util.HashMap;
import java.util.HashSet;

public interface JobListData {
    HashMap<Integer, Job> jobList = new HashMap<>();
    HashMap<Integer, HashSet<Integer>> employerJobList = new HashMap<>();
}
