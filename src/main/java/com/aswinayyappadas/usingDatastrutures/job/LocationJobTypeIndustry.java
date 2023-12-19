package com.aswinayyappadas.usingDatastrutures.job;

import java.util.HashMap;
/**
 * Utility class for mapping location, industry, and job types to their corresponding IDs.
 */
public class LocationJobTypeIndustry {
    /**
     * A map associating location IDs with their corresponding names.
     */
    private HashMap<Integer, String> locationMap = new HashMap<>();
    /**
     * A map associating industry IDs with their corresponding names.
     */
    private HashMap<Integer, String> industryMap = new HashMap<>();
    /**
     * A map associating job type IDs with their corresponding names.
     */
    private HashMap<Integer, String> jobTypeMap = new HashMap<>();
    /**
     * Default constructor that initializes mappings for locations, industries, and job types.
     */
    public LocationJobTypeIndustry() {
        // Location mappings
        locationMap.put(1, "Delhi");
        locationMap.put(2, "Mumbai");
        locationMap.put(3, "Chennai");
        locationMap.put(4, "Kolkata");
        locationMap.put(5, "Bengaluru");
        locationMap.put(6, "Hyderabad");
        locationMap.put(7, "Pune");
        locationMap.put(8, "Ahmedabad");
        locationMap.put(9, "Jaipur");
        locationMap.put(10, "Lucknow");
        locationMap.put(11, "Chandigarh");
        locationMap.put(12, "Bhopal");
        locationMap.put(13, "Patna");
        locationMap.put(14, "Thiruvananthapuram");

        // Job type mappings
        jobTypeMap.put(1, "Full Time");
        jobTypeMap.put(2, "Part Time");
        jobTypeMap.put(3, "Contract");
        jobTypeMap.put(4, "Freelance");
        jobTypeMap.put(5, "Temporary");
        jobTypeMap.put(6, "Internship");
        jobTypeMap.put(7, "Remote");

        // Industry mappings
        industryMap.put(1, "IT");
        industryMap.put(2, "Finance");
        industryMap.put(3, "Healthcare");
        industryMap.put(4, "Education");
        industryMap.put(5, "Manufacturing");
        industryMap.put(6, "Retail");
        industryMap.put(7, "Telecom");
        industryMap.put(8, "Automotive");
        industryMap.put(9, "Hospitality");
        industryMap.put(10, "Energy");
    }

    /**
     * Getter for the location map.
     *
     * @return A map associating location IDs with their corresponding names.
     */
    public HashMap<Integer, String> getLocationMap() {
        return locationMap;
    }
    /**
     * Getter for the industry map.
     *
     * @return A map associating industry IDs with their corresponding names.
     */
    public HashMap<Integer, String> getIndustryMap() {
        return industryMap;
    }
    /**
     * Getter for the job type map.
     *
     * @return A map associating job type IDs with their corresponding names.
     */
    public HashMap<Integer, String> getJobTypeMap() {
        return jobTypeMap;
    }
}
