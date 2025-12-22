package config;

public class Settings {
    public static final int ISLAND_WIDTH = 50;
    public static final int ISLAND_HEIGHT = 20;

    public static final int WOLF = 0;
    public static final int BOA = 1;
    public static final int FOX = 2;
    public static final int BEAR = 3;
    public static final int EAGLE = 4;
    public static final int HORSE = 5;
    public static final int DEER = 6;
    public static final int RABBIT = 7;
    public static final int MOUSE = 8;
    public static final int GOAT = 9;
    public static final int SHEEP = 10;
    public static final int BOAR = 11;
    public static final int BUFFALO = 12;
    public static final int DUCK = 13;
    public static final int CATERPILLAR = 14;
    public static final int PLANT = 15;

    public static final int[][] EAT_PROBABILITY = {
            { -1, 0, 0, 0, 0, 10, 15, 60, 80, 60, 70, 15, 10, 40, 0, 0 },
            { 0, -1, 15, 0, 0, 0, 0, 20, 40, 0, 0, 0, 0, 10, 0, 0 },
            { 0, 0, -1, 0, 0, 0, 0, 70, 90, 0, 0, 0, 0, 60, 40, 0 },
            { 0, 80, 0, -1, 0, 40, 80, 80, 90, 70, 70, 50, 20, 10, 0, 0 },
            { 0, 0, 10, 0, -1, 0, 0, 90, 90, 0, 0, 0, 0, 80, 0, 0 },
            { 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100 },
            { 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 100 },
            { 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 100 },
            { 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 90, 100 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 100 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 100 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 50, 0, 0, -1, 0, 0, 90, 100 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 100 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 90, 100 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 100 }
    };

    public static class AnimalData {
        public double weight;
        public int maxPerCell;
        public int maxSpeed;
        public double foodRequired;

        public AnimalData(double weight, int maxPerCell, int maxSpeed, double foodRequired) {
            this.weight = weight;
            this.maxPerCell = maxPerCell;
            this.maxSpeed = maxSpeed;
            this.foodRequired = foodRequired;
        }
    }

    public static final AnimalData[] ANIMAL_DATA = {
            new AnimalData(50, 30, 3, 8),
            new AnimalData(15, 30, 1, 3),
            new AnimalData(8, 30, 2, 2),
            new AnimalData(500, 5, 2, 80),
            new AnimalData(6, 20, 3, 1),
            new AnimalData(400, 20, 4, 60),
            new AnimalData(300, 20, 4, 50),
            new AnimalData(2, 150, 2, 0.45),
            new AnimalData(0.05, 100, 1, 0.01),
            new AnimalData(60, 140, 3, 10),
            new AnimalData(70, 140, 3, 15),
            new AnimalData(400, 50, 2, 50),
            new AnimalData(700, 10, 3, 100),
            new AnimalData(1, 200, 4, 0.15),
            new AnimalData(0.01, 1000, 0, 0),
            new AnimalData(1, 200, 0, 0)
    };

    public static final int TICK_DURATION_MS = 500;
    public static final int PLANT_GROWTH_RATE = 15;
    public static final int SIMULATION_DURATION_TICKS = 200;
}