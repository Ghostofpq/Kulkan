package com.ghostofpq.kulkan.entities.job.capacity;

import com.ghostofpq.kulkan.entities.job.JobType;

import java.util.ArrayList;

public class UnlockJob extends Capacity {
    private JobType jobType;

    public UnlockJob(String name, String description, JobType jobType, int price) {
        this.prerequisites = new ArrayList<Capacity>();
        this.sons = new ArrayList<Capacity>();
        this.name = name;
        this.description = description;

        this.jobType = jobType;

        this.type = CapacityType.UNLOCK_JOB;

        this.price = price;
        this.locked = true;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
}
