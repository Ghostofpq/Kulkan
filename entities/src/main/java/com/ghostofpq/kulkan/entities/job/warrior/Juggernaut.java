package com.ghostofpq.kulkan.entities.job.warrior;

import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;

public class Juggernaut extends Job {
    private final static String JUGGERNAUT_NAME = "Juggernaut";
    private final static String JUGGERNAUT_DESC = "Juggernaut are stronk";

    public Juggernaut() {
        super(JUGGERNAUT_NAME, JUGGERNAUT_DESC);
        this.jobType = JobType.WARRIOR;
    }
}
