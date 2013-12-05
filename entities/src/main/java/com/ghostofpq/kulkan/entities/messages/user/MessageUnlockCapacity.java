package com.ghostofpq.kulkan.entities.messages.user;


import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;

public class MessageUnlockCapacity extends ClientMessage {
    private String gameCharName;
    private JobType job;
    private String capacityName;

    public MessageUnlockCapacity(String keyToken, String gameCharName, JobType job, String capacityName) {
        this.type = MessageType.CHARACTER_UNLOCK_CAPACITY;
        this.keyToken = keyToken;
        this.gameCharName = gameCharName;
        this.job = job;
        this.capacityName = capacityName;
    }

    public String getGameCharName() {
        return gameCharName;
    }

    public JobType getJob() {
        return job;
    }

    public String getCapacityName() {
        return capacityName;
    }
}
