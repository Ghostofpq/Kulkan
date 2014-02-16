package com.ghostofpq.kulkan.server.database.model;


import com.ghostofpq.kulkan.entities.job.Job;
import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.job.Mage;
import com.ghostofpq.kulkan.entities.job.warrior.Warrior;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class JobStatusDB {
    private JobType jobType;
    private int jobPoints;
    private int cumulativeJobPoints;
    private Map<String, Boolean> skillTreeStatus;

    public JobStatusDB() {
    }

    public JobStatusDB(JobType jobType, int jobPoints, int cumulativeJobPoints, Map<String, Boolean> skillTreeStatus) {
        this.jobType = jobType;
        this.jobPoints = jobPoints;
        this.cumulativeJobPoints = cumulativeJobPoints;
        this.skillTreeStatus = skillTreeStatus;
    }

    public JobStatusDB(Job job) {
        this.jobType = job.getJobType();
        this.jobPoints = job.getJobPoints();
        this.cumulativeJobPoints = job.getCumulativeJobPoints();
        this.skillTreeStatus = job.getSkillTreeStatus();
    }

    public Job toJob() {
        Job job = null;
        switch (jobType) {
            case WARRIOR:
                job = new Warrior();
                break;
            case MAGE:
                job = new Mage();
                break;
        }
        if (null != job) {
            job.setJobPoints(jobPoints);
            job.setCumulativeJobPoints(cumulativeJobPoints);
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

    public int getCumulativeJobPoints() {
        return cumulativeJobPoints;
    }

    public void setCumulativeJobPoints(int cumulativeJobPoints) {
        this.cumulativeJobPoints = cumulativeJobPoints;
    }

    public Map<String, Boolean> getSkillTreeStatus() {
        return skillTreeStatus;
    }

    public void setSkillTreeStatus(Map<String, Boolean> skillTreeStatus) {
        this.skillTreeStatus = skillTreeStatus;
    }
}
