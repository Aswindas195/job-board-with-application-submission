package com.aswinayyappadas.usingDatastrutures.util.job;

import com.aswinayyappadas.usingDatastrutures.job.Job;


import java.util.HashMap;

public class CheckJobIdValidity {
    public boolean isValidJobId(int jobId, HashMap<Integer, Job> jobList) {
        return jobList.containsKey(jobId);
    }
}
