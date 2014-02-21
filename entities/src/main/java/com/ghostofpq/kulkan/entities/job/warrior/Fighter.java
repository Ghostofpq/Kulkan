package com.ghostofpq.kulkan.entities.job.warrior;

import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Fighter extends Job {
    private final static String FIGHTER_NAME = "Fighter";
    private final static String FIGHTER_DESC = "Fighter are stronk";

    public Fighter() {
        super(FIGHTER_NAME, FIGHTER_DESC);
        this.jobType = JobType.FIGHTER;
    }
}
