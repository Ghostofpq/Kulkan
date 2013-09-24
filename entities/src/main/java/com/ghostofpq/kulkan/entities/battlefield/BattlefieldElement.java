package com.ghostofpq.kulkan.entities.battlefield;

import java.io.Serializable;

public abstract class BattlefieldElement implements Serializable {
    private static final long serialVersionUID = 3537734489408631256L;


    protected BattlefieldElementType type;

    public enum BattlefieldElementType {
        BLOC
    }

    /**
     * Getters and Setters
     */

    public BattlefieldElementType getType() {
        return type;
    }
}
