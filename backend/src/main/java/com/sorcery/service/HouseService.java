package com.sorcery.service;

import com.sorcery.model.Rules;
import com.sorcery.model.AdmissionResult;
import com.sorcery.model.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.stereotype.Service;

@Service
public class HouseService {
    // Puntuación casa para candidato
    private int calculateHouseScore(Application app, Rules.House house) {
        int score = 0;
        Rules.HousePoints points = house.getPoints();

        // Virtud
        if (points.getVirtue() != null) {
            score += points.getVirtue().getOrDefault(app.getVirtue(), 0);
        }

        // Debilidad
        if (points.getWeakness() != null) {
            score += points.getWeakness().getOrDefault(app.getWeakness(), 0);
        }

        // Familia
        if (points.getFamily() != null) {
            score += points.getFamily().getOrDefault(app.getFamilyName(), 0);
        }

        return score;
    }

    // Asignar la mejor casa
    public String assignHouse(Application app, Rules rules) {
        String bestHouse = null;
        int bestScore = Integer.MIN_VALUE;

        for (Rules.House house : rules.getHouses()) {
            int score = calculateHouseScore(app, house);

            if (score > bestScore) {
                bestScore = score;
                bestHouse = house.getName();
            }
        }
        return bestHouse;
    }

    // Fixing the house scores calculation to return a list of house scores for each
    // applicant
    public List<AdmissionResult.HouseScore> calculateHouseScores(Application app, Rules rules) {
        List<AdmissionResult.HouseScore> houseScores = new ArrayList<>();
        for (Rules.House house : rules.getHouses()) {
            int score = calculateHouseScore(app, house);
            houseScores.add(new AdmissionResult.HouseScore(house.getName(), score));
        }
        return houseScores;

    }

    // Crear un Set para guardar todas las casas disponibles aunque no estén
    // asignadas a ningun candidato, no se pueden repetir pero da igual
    // orden(HashSet).
    public Set<String> getAllHouses(Rules rules) {
        Set<String> allHouses = new HashSet<>();
        for (Rules.House house : rules.getHouses()) {
            allHouses.add(house.getName());
        }
        return allHouses;
    }
}
