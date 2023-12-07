package com.aswinayyappadas.usingDatastrutures.joblistings;

import com.aswinayyappadas.usingDatastrutures.job.Job;

import java.util.HashMap;

public interface JobListData {
    HashMap<Integer, Job> jobList = new HashMap<>();
    HashMap<Integer, Job> employerJobList = new HashMap<>();
}
