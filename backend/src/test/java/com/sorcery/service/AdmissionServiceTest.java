package com.sorcery.service;

import com.sorcery.model.AdmissionResult;
import com.sorcery.model.Application;
import com.sorcery.model.RejectionReason;
import com.sorcery.model.Rules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdmissionServiceTest {
    private AdmissionService admissionService;
    private HouseService houseService;
    private Rules rules;

    @BeforeEach
    void setUp() {
        admissionService = new AdmissionService();
        houseService = new HouseService();
        rules = buildTestRules();
    }

    // VETOES
    // Family ban
    @Test
    void bannedFamilyShouldBeRejected() {
        Application app = buildApp("A001", "Ciro", "Blackcrow", 13,
                "wisdom", "shyness", "2026-02-01");

        List<AdmissionResult> results = admissionService.processAdmissions(List.of(app), rules);

        assertEquals(1, results.size());
        assertEquals("REJECTED", results.get(0).getStatus());
        assertEquals(RejectionReason.BANNED_FAMILY,
                results.get(0).getRejectionReason());
    }

    // Too young ban
    @Test
    void ageTooYoungShouldBeRejected() {
        Application app = buildApp("A002", "Frida", "Vexley", 9,
                "cunning", "laziness", "2026-02-01");

        List<AdmissionResult> results = admissionService.processAdmissions(List.of(app), rules);

        assertEquals(RejectionReason.OUT_OF_AGE,
                results.get(0).getRejectionReason());
    }

    // Too old ban
    @Test
    void ageTooOldShouldBeRejected() {
        Application app = buildApp("A003", "Nera", "Underhill", 19,
                "loyalty", "pride", "2026-02-01");

        List<AdmissionResult> results = admissionService.processAdmissions(List.of(app), rules);

        assertEquals(RejectionReason.OUT_OF_AGE,
                results.get(0).getRejectionReason());
    }

    // Weakness ban
    @Test
    void unacceptableWeaknessShouldBeRejected() {
        Application app = buildApp("A004", "Bruno", "Grimsby", 13,
                "cunning", "cruelty", "2026-02-01");

        List<AdmissionResult> results = admissionService.processAdmissions(List.of(app), rules);

        assertEquals(RejectionReason.UNACCEPTABLE_WEAKNESS,
                results.get(0).getRejectionReason());
    }

    // Date range application ban
    @Test
    void applicationOutsideDateRangeShouldBeRejected() {
        Application app = buildApp("A005", "Yago", "Kettleby", 13,
                "wit", "laziness", "2025-12-01");

        List<AdmissionResult> results = admissionService.processAdmissions(List.of(app), rules);

        assertEquals(RejectionReason.OUT_OF_DATE,
                results.get(0).getRejectionReason());
    }

    // Test for multiple vetoes
    @Test
    void whenMultipleVetoesApplyFirstOneTakesPriority() {
        // Blackcrow (banned) AND age 9 (out of age) AND cruelty (bad weakness)
        Application app = buildApp("A006", "Elo", "Blackcrow", 9,
                "cunning", "cruelty", "2026-02-01");

        List<AdmissionResult> results = admissionService.processAdmissions(List.of(app), rules);

        // BANNED_FAMILY is first in the veto order
        assertEquals(RejectionReason.BANNED_FAMILY,
                results.get(0).getRejectionReason());
    }

    // INVITATIONS
    // Test invitation even with banned family
    @Test
    void invitedCandidateIsAcceptedEvenWithBannedFamily() {
        // Tobias Figgleworth is invited — age 19 and date outside range
        Application tobias = buildApp("A031", "Tobias", "Figgleworth", 19,
                "patience", "laziness", "2026-05-02");

        List<AdmissionResult> results = admissionService.processAdmissions(List.of(tobias), rules);

        assertEquals("ACCEPTED", results.get(0).getStatus());
        assertTrue(results.get(0).isInvitedByHeadmaster());
    }

    // SCORES
    // Test to see if score is calculated correctly
    @Test
    void scoreIsCalculatedCorrectly() {
        Application app = buildApp("A007", "Ilde", "Nutwood", 11,
                "cunning", "untidiness", "2026-02-01");

        int score = admissionService.calculateScore(app, rules);

        assertEquals(28, score);
    }

    @Test
    void unknownVirtueScoresZero() {
        // "wisdom" is not in our test rules points map
        Application app = buildApp("A008", "Delia", "Dunmere", 13,
                "wisdom", "shyness", "2026-02-01");

        int score = admissionService.calculateScore(app, rules);

        // 0 (wisdom) + 0 (Dunmere family) + -2 (shyness) + 5 (age 11-13) = 3
        assertEquals(3, score);
    }

    @Test
    void unknownWeaknessScoresZero() {
        // "gossip" is not in our test rules weakness map
        Application app = buildApp("A009", "Lumi", "Mossbank", 13,
                "cunning", "gossip", "2026-02-01");

        int score = admissionService.calculateScore(app, rules);

        // 12 (cunning) + 0 (Mossbank) + 0 (gossip not in map) + 5 (age 11-13) = 17
        assertEquals(17, score);
    }

    // SORTING BY
    @Test
    void higherScoreCandidateRanksFirst() {
        Application highScore = buildApp("A010", "Ilde", "Nutwood", 11,
                "cunning", "untidiness", "2026-02-01"); // score 28

        Application lowScore = buildApp("A011", "Delia", "Dunmere", 13,
                "wisdom", "shyness", "2026-02-01"); // score 3

        List<AdmissionResult> results = admissionService.processAdmissions(
                List.of(lowScore, highScore), rules); // intentionally reversed

        List<AdmissionResult> accepted = results.stream()
                .filter(r -> "ACCEPTED".equals(r.getStatus()))
                .sorted((a, b) -> Integer.compare(
                        a.getPosition() != null ? a.getPosition() : 0,
                        b.getPosition() != null ? b.getPosition() : 0))
                .toList();

        assertEquals("A010", accepted.get(0).getApplication().getId());
        assertEquals("A011", accepted.get(1).getApplication().getId());
    }

    @Test
    void tieBreakByAgeYoungerGoesFirst() {
        // Same score: cunning(12) + 0 family + shyness(-2) + age varies
        // age 11 → +5 = 15 total, age 13 → +5 = 15 total (same age group)
        // Use age 11 vs 13 — same points group but younger wins tiebreak
        Application younger = buildApp("A012", "Wren", "Dunmere", 11,
                "cunning", "shyness", "2026-02-01");
        Application older = buildApp("A013", "Lorca", "Dunmere", 13,
                "cunning", "shyness", "2026-02-01");

        List<AdmissionResult> results = admissionService.processAdmissions(
                List.of(older, younger), rules);

        List<AdmissionResult> accepted = results.stream()
                .filter(r -> "ACCEPTED".equals(r.getStatus()))
                .sorted((a, b) -> Integer.compare(
                        a.getPosition() != null ? a.getPosition() : 0,
                        b.getPosition() != null ? b.getPosition() : 0))
                .toList();

        assertEquals("A012", accepted.get(0).getApplication().getId());
    }

    @Test
    void tieBreakByFamilyNameAlphabetically() {
        // Same score and same age — family name decides
        Application abel = buildApp("A014", "Iker", "Abel", 13,
                "cunning", "shyness", "2026-02-01");
        Application zorro = buildApp("A015", "Iker", "Zorro", 13,
                "cunning", "shyness", "2026-02-01");

        List<AdmissionResult> results = admissionService.processAdmissions(
                List.of(zorro, abel), rules);

        List<AdmissionResult> accepted = results.stream()
                .filter(r -> "ACCEPTED".equals(r.getStatus()))
                .sorted((a, b) -> Integer.compare(
                        a.getPosition() != null ? a.getPosition() : 0,
                        b.getPosition() != null ? b.getPosition() : 0))
                .toList();

        assertEquals("A014", accepted.get(0).getApplication().getId());
    }

    // HOUSES
    @Test
    void houseWithHighestScoreWinsCandidate() {
        // courage → Lion gets 8 points, others get 0
        Application app = buildApp("A016", "Fusco", "Dunmere", 13,
                "courage", "shyness", "2026-02-01");

        String house = houseService.assignHouse(app, rules);

        assertEquals("Lion", house);
    }

    @Test
    void onTieFirstHouseInJsonWins() {
        // "wisdom" not in any house points — all houses score 0
        Application app = buildApp("A017", "Delia", "Dunmere", 13,
                "wisdom", "vanity", "2026-02-01");

        String house = houseService.assignHouse(app, rules);

        // Lion is first in the houses array
        assertEquals("Lion", house);
    }

    // HELPERS
    private Application buildApp(String id, String firstName,
            String familyName, int age, String virtue,
            String weakness, String date) {
        Application app = new Application();
        app.setId(id);
        app.setFirstName(firstName);
        app.setFamilyName(familyName);
        app.setAge(age);
        app.setVirtue(virtue);
        app.setWeakness(weakness);
        app.setApplicationDate(LocalDate.parse(date));
        return app;
    }

    private List<Application> buildNValidApps(int n) {
        List<Application> apps = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            apps.add(buildApp(
                    "X" + i, "Name" + i, "Family" + i, 13,
                    "cunning", "shyness", "2026-02-01"));
        }
        return apps;
    }

    private Rules buildTestRules() {
        Rules rules = new Rules();

        // Application dates
        Rules.ApplicationDates dates = new Rules.ApplicationDates();
        dates.setFrom(LocalDate.of(2026, 1, 7));
        dates.setTo(LocalDate.of(2026, 3, 31));
        rules.setApplicationDates(dates);

        // Age range
        Rules.AgeRange ageRange = new Rules.AgeRange();
        ageRange.setMin(11);
        ageRange.setMax(17);
        rules.setAgeRange(ageRange);

        // Places
        rules.setPlaces(50);

        // Banned families
        rules.setBannedFamilies(List.of("Blackcrow", "Greenbeard"));

        // Unacceptable weaknesses
        rules.setUnacceptableWeaknesses(List.of("cruelty", "treachery"));

        // Invitations
        rules.setInvitations(List.of("Tobias Figgleworth"));

        // Points
        Rules.Points points = new Rules.Points();
        points.setVirtue(Map.of(
                "cunning", 12,
                "wit", 10,
                "courage", 8,
                "loyalty", 8,
                "curiosity", 6,
                "ambition", 6,
                "patience", 4,
                "kindness", 4));
        points.setFamily(Map.of(
                "Nutwood", 15,
                "Marlowe", 10,
                "Yewbank", 5));
        points.setWeakness(Map.of(
                "shyness", -2,
                "untidiness", -4,
                "impatience", -6,
                "laziness", -8,
                "pride", -10));

        Rules.AgePoints youngGroup = new Rules.AgePoints();
        youngGroup.setFrom(11);
        youngGroup.setTo(13);
        youngGroup.setPoints(5);

        Rules.AgePoints midGroup = new Rules.AgePoints();
        midGroup.setFrom(14);
        midGroup.setTo(15);
        midGroup.setPoints(2);

        Rules.AgePoints oldGroup = new Rules.AgePoints();
        oldGroup.setFrom(16);
        oldGroup.setTo(17);
        oldGroup.setPoints(0);

        points.setAge(List.of(youngGroup, midGroup, oldGroup));
        rules.setPoints(points);

        // Houses
        Rules.House lion = new Rules.House();
        lion.setName("Lion");
        Rules.HousePoints lionPoints = new Rules.HousePoints();
        lionPoints.setVirtue(Map.of("courage", 8, "loyalty", 5));
        lionPoints.setWeakness(Map.of("shyness", 4));
        lionPoints.setFamily(Map.of("Marlowe", 4));
        lion.setPoints(lionPoints);

        Rules.House serpent = new Rules.House();
        serpent.setName("Serpent");
        Rules.HousePoints serpentPoints = new Rules.HousePoints();
        serpentPoints.setVirtue(Map.of("ambition", 8, "cunning", 5));
        serpentPoints.setWeakness(Map.of("pride", 4));
        serpentPoints.setFamily(Map.of("Ferris", 4));
        serpent.setPoints(serpentPoints);

        Rules.House raven = new Rules.House();
        raven.setName("Raven");
        Rules.HousePoints ravenPoints = new Rules.HousePoints();
        ravenPoints.setVirtue(Map.of("wit", 8, "curiosity", 5));
        ravenPoints.setWeakness(Map.of("untidiness", 4));
        ravenPoints.setFamily(Map.of("Yewbank", 4));
        raven.setPoints(ravenPoints);

        Rules.House badger = new Rules.House();
        badger.setName("Badger");
        Rules.HousePoints badgerPoints = new Rules.HousePoints();
        badgerPoints.setVirtue(Map.of("kindness", 8, "patience", 5));
        badgerPoints.setWeakness(Map.of("laziness", 4));
        badgerPoints.setFamily(Map.of("Nutwood", 4));
        badger.setPoints(badgerPoints);

        rules.setHouses(List.of(lion, serpent, raven, badger));

        return rules;
    }
}
