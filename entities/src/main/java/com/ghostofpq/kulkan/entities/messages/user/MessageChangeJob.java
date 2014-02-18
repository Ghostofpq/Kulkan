package com.ghostofpq.kulkan.entities.messages.user;

import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.bson.types.ObjectId;

public class MessageChangeJob extends ClientMessage {
    private ObjectId gameCharId;
    private JobType newJob;
    private String username;

    public MessageChangeJob(String keyToken, String username, ObjectId gameCharId, JobType newJob) {
        this.type = MessageType.CHARACTER_CHANGE_JOB;
        this.keyToken = keyToken;
        this.username = username;
        this.gameCharId = gameCharId;
        this.newJob = newJob;
    }

    public ObjectId getGameCharId() {
        return gameCharId;
    }

    public JobType getNewJob() {
        return newJob;
    }

    public String getUsername() {
        return username;
    }
}
