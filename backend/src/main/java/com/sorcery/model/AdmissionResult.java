package com.sorcery.model;

import lombok.Data;

@Data
public class AdmissionResult {
    private Application application;
    private int score;
    private Integer position;
    private String status;
    private RejectionReason rejectionReason;
    private String rejectionDetail;
    private String house;
    private boolean invitedByHeadmaster;
    private int virtuePoints;
    private int familyPoints;
    private int weaknessPoints;
    private int agePoints;

    public static AdmissionResult accepted(Application app, int score, int position, String house, boolean invited,
            int virtuePoints, int familyPoints,
            int weaknessPoints, int agePoints) {
        AdmissionResult result = new AdmissionResult();
        result.application = app;
        result.score = score;
        result.position = position;
        result.status = "ACCEPTED";
        result.house = house;
        result.invitedByHeadmaster = invited;
        result.virtuePoints = virtuePoints;
        result.familyPoints = familyPoints;
        result.weaknessPoints = weaknessPoints;
        result.agePoints = agePoints;
        return result;
    }

    public static AdmissionResult rejected(Application app, int score, Integer position, RejectionReason reason,
            String detail,
            int virtuePoints, int familyPoints,
            int weaknessPoints, int agePoints) {
        AdmissionResult result = new AdmissionResult();
        result.application = app;
        result.score = score;
        result.position = position;
        result.status = "REJECTED";
        result.rejectionReason = reason;
        result.rejectionDetail = detail;
        result.virtuePoints = virtuePoints;
        result.familyPoints = familyPoints;
        result.weaknessPoints = weaknessPoints;
        result.agePoints = agePoints;
        return result;
    }
}
