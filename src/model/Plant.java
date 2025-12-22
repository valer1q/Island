package model;

import config.Settings;
import java.util.Random;

public class Plant {
    private double weight;
    private boolean isAlive;
    private int growthStage;
    private Random random;

    public Plant() {
        this.weight = Settings.ANIMAL_DATA[Settings.PLANT].weight;
        this.isAlive = true;
        this.growthStage = 1;
        this.random = new Random();
    }

    public void grow() {
        if (isAlive) {
            growthStage++;
            weight += 0.1;

            if (growthStage >= 5 && random.nextInt(100) < 10) {
                growthStage = 3;
            }
        }
    }

    public boolean eat() {
        if (isAlive) {
            isAlive = false;
            return true;
        }
        return false;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public String getSymbol() {
        switch(growthStage) {
            case 1: return "🌱";
            case 2: return "🌿";
            case 3: return "☘️";
            case 4: return "🍀";
            default: return "🌳";
        }
    }
}