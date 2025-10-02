package com.store.management.api.model;

/**
 * User roles with hierarchical permissions
 */
public enum Role {
    USER(1),
    MANAGER(2),
    ADMIN(3);
    
    private final int level;
    
    Role(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
}