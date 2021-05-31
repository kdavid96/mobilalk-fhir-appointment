package com.example.fhirappointment;

public class Participant {
    private enum Type{
        secondaryPerformer, primaryPerformer, participation
    }

    private enum Actor{
        patient, practitioner, practitionerRole, relatedPerson, device, healthcareService, location
    }

    private enum Required{
        required, optional, informationOnly
    }

    private enum Status{
        accepted, declined, tentative, needsAction
    }

    private String email;

    private String uid;

    private Type type;

    private Actor actor;

    private Required required;

    private Status status;

    public void setEmail(String email) {
        this.email = email;
    }

    public Participant(String email, String uid, String type, String actor, String required, String status) {
        this.email = email;
        this.uid = uid;
        this.type = Type.valueOf(type);
        this.actor = Actor.valueOf(actor);
        this.required = Required.valueOf(required);
        this.status = Status.valueOf(status);
    }

    public Participant(String email, String uid) {
        this.email = email;
        this.uid = uid;
        this.type = Type.participation;
        this.actor = Actor.patient;
        this.required = Required.informationOnly;
        this.status = Status.tentative;
    }

    public Participant() {
        this.email = "def constructor";
        this.uid = "def constructor";
        this.type = Type.participation;
        this.actor = Actor.patient;
        this.required = Required.informationOnly;
        this.status = Status.tentative;
    }

    public void Set(String email, String uid, String type, String actor, String required, String status){
        this.email = email;
        this.uid = uid;
        this.type = Type.valueOf(type);
        this.actor = Actor.valueOf(actor);
        this.required = Required.valueOf(required);
        this.status = Status.valueOf(status);
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
