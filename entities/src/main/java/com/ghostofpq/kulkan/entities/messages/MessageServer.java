package com.ghostofpq.kulkan.entities.messages;

import java.io.Serializable;
import java.util.List;

public class MessageServer extends Message implements Serializable {
    protected List<String> targetList;
}
