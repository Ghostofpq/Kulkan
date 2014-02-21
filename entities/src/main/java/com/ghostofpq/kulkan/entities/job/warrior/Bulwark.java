package com.ghostofpq.kulkan.entities.job.warrior;

import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Bulwark extends Job {
    private final static String BULWARK_NAME = "Bulwark";
    private final static String BULWARK_DESC = "Bulwark are stronk";

    public Bulwark() {
        super(BULWARK_NAME, BULWARK_DESC);
        this.jobType = JobType.BULWARK;
    }
}
