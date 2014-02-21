package com.ghostofpq.kulkan.entities.job.warrior;

import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Defender extends Job {
    private final static String DEFENDER_NAME = "Defender";
    private final static String DEFENDER_DESC = "Defender are stronk";

    public Defender() {
        super(DEFENDER_NAME, DEFENDER_DESC);
        this.jobType = JobType.DEFENDER;
    }
}
