package com.ghostofpq.kulkan.entities.job.warrior;

import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Champion extends Job {
    private final static String CHAMPION_NAME = "Champion";
    private final static String CHAMPION_DESC = "Champion are stronk";

    public Champion() {
        super(CHAMPION_NAME, CHAMPION_DESC);
        this.jobType = JobType.CHAMPION;
    }
}
