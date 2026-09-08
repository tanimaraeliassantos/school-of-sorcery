package com.sorcery.model;

public class AdmissionResult {
    private Application application;
    private int score;
    private Integer position;
    private String status;
    private RejectionReason rejectionReason;
    private String rejectionDetail;
    private String house;
    private boolean invitedByHeadmaster;

    public static AdmissionResult accepted(Application app, int score, int position, String house, boolean invited) {
        AdmissionResult result = new AdmissionResult();
        result.application = app;
        result.score = score;
        result.position = position;
        result.status = "ACCEPTED";
        result.house = house;
        result.invitedByHeadmaster = invited;
        return result;
    }

    public static AdmissionResult rejected(Application app, int score, Integer position, RejectionReason reason,
            String detail) {
        AdmissionResult result = new AdmissionResult();
        result.application = app;
        result.score = score;
        result.position = position;
        result.status = "REJECTED";
        result.rejectionReason = reason;
        result.rejectionDetail = detail;
        return result;  
    }
}
