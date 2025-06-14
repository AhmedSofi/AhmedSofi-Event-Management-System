package com.ems.observers;

import com.ems.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of EventSubject for event notifications.
 */
public class EventNotifier implements EventSubject {
    private Event event;
    private List<EventObserver> observers = new ArrayList<>();

    public EventNotifier(Event event) {
        this.event = event;
    }

    @Override
    public void registerObserver(EventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (EventObserver observer : observers) {
            observer.update(event, message);
        }
    }

    // Business methods that trigger notifications
    public void updateEventDetails(String changes) {
        // Update logic here
        notifyObservers("Event details updated: " + changes);
    }

    public void cancelEvent() {
        event.setStatus(Event.EventStatus.CANCELLED);
        notifyObservers("Event has been cancelled");
    }
}
