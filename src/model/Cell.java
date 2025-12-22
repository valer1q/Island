package model;

import model.animals.Animal;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Random;

public class Cell {
    private List<Animal> animals;
    private List<Plant> plants;
    private Random random;
    private int x, y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.animals = new CopyOnWriteArrayList<>();
        this.plants = new CopyOnWriteArrayList<>();
        this.random = new Random();

        initializePlants();
    }

    private void initializePlants() {
        int initialPlantCount = random.nextInt(10) + 5;
        for (int i = 0; i < initialPlantCount; i++) {
            plants.add(new Plant());
        }
    }

    public void addAnimal(Animal animal) {
        if (canAddAnimal(animal.getType())) {
            animals.add(animal);
        }
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void addPlant() {
        if (plants.size() < config.Settings.ANIMAL_DATA[config.Settings.PLANT].maxPerCell) {
            plants.add(new Plant());
        }
    }

    public void addPlants(int count) {
        for (int i = 0; i < count; i++) {
            if (plants.size() < config.Settings.ANIMAL_DATA[config.Settings.PLANT].maxPerCell) {
                plants.add(new Plant());
            }
        }
    }

    public boolean eatPlant() {
        for (Plant plant : plants) {
            if (plant.isAlive()) {
                plant.eat();
                return true;
            }
        }
        return false;
    }

    public int getPlantCount() {
        int count = 0;
        for (Plant plant : plants) {
            if (plant.isAlive()) {
                count++;
            }
        }
        return count;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void clearDeadAnimals() {
        animals.removeIf(animal -> !animal.isAlive());
    }

    public void clearDeadPlants() {
        plants.removeIf(plant -> !plant.isAlive());
    }

    public void growPlants() {
        for (Plant plant : plants) {
            if (plant.isAlive()) {
                plant.grow();
            }
        }

        if (random.nextInt(100) < 5 && plants.size() <
                config.Settings.ANIMAL_DATA[config.Settings.PLANT].maxPerCell) {
            plants.add(new Plant());
        }
    }

    public boolean canAddAnimal(int type) {
        long animalsOfType = animals.stream()
                .filter(a -> a.getType() == type && a.isAlive())
                .count();
        return animalsOfType < config.Settings.ANIMAL_DATA[type].maxPerCell;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getCellSymbol() {
        if (!animals.isEmpty()) {
            return animals.get(0).getSymbol();
        } else if (getPlantCount() > 0) {
            int maxStage = 0;
            String symbol = "·";
            for (Plant plant : plants) {
                if (plant.isAlive() && plant.getGrowthStage() > maxStage) {
                    maxStage = plant.getGrowthStage();
                    symbol = plant.getSymbol();
                }
            }
            return symbol;
        } else {
            return "·";
        }
    }
}