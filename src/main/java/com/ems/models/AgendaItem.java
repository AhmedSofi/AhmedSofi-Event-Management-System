package com.ems.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AgendaItem {
    private int agendaItemId;
    private int eventId;
    private String title;
    private String description;

    public AgendaItem() {
    }

    public AgendaItem(int agendaItemId, int eventId, String title, String description) {
        this.agendaItemId = agendaItemId;
        this.eventId = eventId;
        this.title = title;
        this.description = description;

    }

    public int getAgendaItemId() {
        return agendaItemId;
    }

    public void setAgendaItemId(int agendaItemId) {
        this.agendaItemId = agendaItemId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}