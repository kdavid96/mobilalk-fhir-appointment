package com.example.fhirappointment;

import java.util.Date;

public class Appointment {
    private String id;

    private String title;

    private enum Status{
        proposed, pending, booked, arrived, fulfilled, cancelled, noshow, enteredInError, checkedIn, waitlist
    };

    private Status status;

    //private Participant participant;

    private String participant;

    private Date start;

    private Date end;

    //private Participant practitioner;

    private String practitioner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }*/

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public Appointment(String id, String title, String participant, Date start, Date end, String practitioner) {
        this.id = id;
        this.title = title;
        this.participant = participant;
        this.start = start;
        this.end = end;
        this.practitioner = practitioner;
        this.status = Status.proposed;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    /*public Participant getPractitioner() {
        return this.practitioner;
    }

    public void setPractitioner(Participant practitioner) {
        this.practitioner = practitioner;
    }*/

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getPractitioner() {
        return practitioner;
    }

    public void setPractitioner(String practitioner) {
        this.practitioner = practitioner;
    }
}
