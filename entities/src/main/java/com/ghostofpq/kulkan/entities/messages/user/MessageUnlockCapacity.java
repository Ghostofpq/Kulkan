package com.ghostofpq.kulkan.entities.messages.user;

import com.ghostofpq.kulkan.entities.job.JobType;
import com.ghostofpq.kulkan.entities.messages.ClientMessage;
import com.ghostofpq.kulkan.entities.messages.MessageType;
import org.bson.types.ObjectId;

public class MessageUnlockCapacity extends ClientMessage {
    private ObjectId gameCharId;
    private JobType job;
    private String capacityName;

    public MessageUnlockCapacity(String keyToken, String username, ObjectId gameCharId, JobType job, String capacityName) {
        this.type = MessageType.CHARACTER_UNLOCK_CAPACITY;
        this.keyToken = keyToken;
        this.username = username;
        this.gameCharId = gameCharId;
        this.job = job;
        this.capacityName = capacityName;
    }

    public ObjectId getGameCharId() {
        return gameCharId;
    }

    public JobType getJob() {
        return job;
    }

    public String getCapacityName() {
        return capacityName;
    }
}
