package com.aswinayyappadas.usingDatastrutures.applications;

import java.util.HashMap;
import java.util.HashSet;

public interface ApplicationsDataList {
    HashMap<Integer, Application> applicationList = new HashMap<>();
    HashMap<Integer, HashSet<Integer>> jobseekerApplicationList = new HashMap<>();
    HashMap<Integer, HashSet<Integer>> jobApplicationsList = new HashMap<>();
}
