package com.sorcery.service;

import com.sorcery.model.Rules;
import com.sorcery.model.Application;

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

}
