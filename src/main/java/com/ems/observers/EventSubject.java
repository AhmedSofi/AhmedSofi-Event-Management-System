package com.ems.observers;

/**
 * Subject interface for the Observer pattern.
 */
public interface EventSubject {
    void registerObserver(EventObserver observer);
    void removeObserver(EventObserver observer);
    void notifyObservers(String message);
}
