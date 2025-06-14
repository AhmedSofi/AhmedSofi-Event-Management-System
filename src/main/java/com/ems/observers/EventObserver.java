package com.ems.observers;

import com.ems.models.Event;

/**
 * Observer interface for the Observer pattern.
 */
public interface EventObserver {
    void update(Event event, String message);
}
