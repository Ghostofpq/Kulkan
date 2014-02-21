package com.ghostofpq.kulkan.entities.job.warrior;

import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Berserk extends Job {
    private final static String BERSERK_NAME = "Berserk";
    private final static String BERSERK_DESC = "Berserk are stronk";

    public Berserk() {
        super(BERSERK_NAME, BERSERK_DESC);
        this.jobType = JobType.BERSERK;
    }
}
