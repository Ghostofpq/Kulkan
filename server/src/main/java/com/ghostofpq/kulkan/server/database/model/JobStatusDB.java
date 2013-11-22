package com.ghostofpq.kulkan.server.database.model;


import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.Warrior;

import java.util.Map;

public class JobStatusDB {
    private JobType jobType;
    private int jobPoints;
    private int cumulatedJobPoints;
    private Map<String, Boolean> skillTreeStatus;

    public JobStatusDB(JobType jobType, int jobPoints, int cumulatedJobPoints, Map<String, Boolean> skillTreeStatus) {
        this.jobType = jobType;
        this.jobPoints = jobPoints;
        this.cumulatedJobPoints = cumulatedJobPoints;
        this.skillTreeStatus = skillTreeStatus;
    }

    public JobStatusDB(Job job) {
        this.jobType = job.getJobType();
        this.jobPoints = job.getJobPoints();
        this.cumulatedJobPoints = job.getCumulativeJobPoints();
        this.skillTreeStatus = job.getSkillTreeStatus();
    }

    public Job toJob() {
        Job job = null;
        switch (jobType) {
            case WARRIOR:
                job = new Warrior();
                break;
        }
        if (null != job) {
            job.setJobPoints(jobPoints);
            job.setCumulativeJobPoints(cumulatedJobPoints);
            job.setSkillTreeStatus(skillTreeStatus);
        }
        return job;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public int getJobPoints() {
        return jobPoints;
    }

    public void setJobPoints(int jobPoints) {
        this.jobPoints = jobPoints;
    }

    public int getCumulatedJobPoints() {
        return cumulatedJobPoints;
    }

    public void setCumulatedJobPoints(int cumulatedJobPoints) {
        this.cumulatedJobPoints = cumulatedJobPoints;
    }

    public Map<String, Boolean> getSkillTreeStatus() {
        return skillTreeStatus;
    }

    public void setSkillTreeStatus(Map<String, Boolean> skillTreeStatus) {
        this.skillTreeStatus = skillTreeStatus;
    }
}
