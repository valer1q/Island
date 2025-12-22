package model.animals;

import config.Settings;
import model.Cell;
import java.util.List;

public class Herbivores {

    public static abstract class Herbivore extends Animal {

        public Herbivore(int x, int y, int type, int maxAge) {
            super(x, y, type, maxAge);
        }

        @Override
        public void eat(Cell cell) {
            if (satiety >= Settings.ANIMAL_DATA[type].foodRequired * 1.5) {
                return;
            }

            if (cell.getPlantCount() > 0) {
                if (cell.eatPlant()) {
                    satiety += 5;
                }
            }

            eatSpecialFood(cell);

            if (satiety < Settings.ANIMAL_DATA[type].foodRequired) {
                satiety -= Settings.ANIMAL_DATA[type].foodRequired * 0.05;
            }
        }

        protected void eatSpecialFood(Cell cell) {
        }

        @Override
        public void move(Cell[][] island) {
            if (!isAlive) return;

            int maxSpeed = Settings.ANIMAL_DATA[type].maxSpeed;

            int bestX = x;
            int bestY = y;
            int maxPlants = island[y][x].getPlantCount();

            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int checkY = y + dy;
                    int checkX = x + dx;

                    if (checkY >= 0 && checkY < Settings.ISLAND_HEIGHT &&
                            checkX >= 0 && checkX < Settings.ISLAND_WIDTH) {
                        int plants = island[checkY][checkX].getPlantCount();
                        if (plants > maxPlants) {
                            maxPlants = plants;
                            bestX = checkX;
                            bestY = checkY;
                        }
                    }
                }
            }

            if (maxPlants > island[y][x].getPlantCount()) {
                Cell oldCell = island[y][x];
                Cell newCell = island[bestY][bestX];

                if (newCell.getAnimals().size() < Settings.ANIMAL_DATA[type].maxPerCell) {
                    oldCell.removeAnimal(this);
                    this.x = bestX;
                    this.y = bestY;
                    newCell.addAnimal(this);
                }
            } else {
                int newX = x + random.nextInt(maxSpeed * 2 + 1) - maxSpeed;
                int newY = y + random.nextInt(maxSpeed * 2 + 1) - maxSpeed;

                newX = Math.max(0, Math.min(newX, Settings.ISLAND_WIDTH - 1));
                newY = Math.max(0, Math.min(newY, Settings.ISLAND_HEIGHT - 1));

                Cell oldCell = island[y][x];
                Cell newCell = island[newY][newX];

                if (newCell.getAnimals().size() < Settings.ANIMAL_DATA[type].maxPerCell) {
                    oldCell.removeAnimal(this);
                    this.x = newX;
                    this.y = newY;
                    newCell.addAnimal(this);
                }
            }
        }

        @Override
        public void reproduce(Cell cell) {
            if (!isAlive || !canReproduce) return;

            if (satiety >= Settings.ANIMAL_DATA[type].foodRequired * 1.2) {
                int reproductionChance = Math.min(15, age / 2);
                if (random.nextInt(100) < reproductionChance) {
                    List<Animal> animals = cell.getAnimals();
                    int sameTypeCount = 0;
                    Animal partner = null;

                    for (Animal animal : animals) {
                        if (animal.getType() == type && animal != this && animal.isAlive()
                                && ((Herbivore) animal).canReproduce) {
                            sameTypeCount++;
                            partner = animal;
                        }
                    }

                    if (sameTypeCount >= 1 && partner != null &&
                            animals.size() < Settings.ANIMAL_DATA[type].maxPerCell) {
                        if (getPopulationDensity(cell) < 0.7) {
                            Animal baby = createBaby();
                            if (baby != null) {
                                cell.addAnimal(baby);
                                satiety *= 0.7;
                                ((Herbivore) partner).satiety *= 0.7;
                            }
                        }
                    }
                }
            }
        }

        protected abstract Animal createBaby();

        protected double getPopulationDensity(Cell cell) {
            int animalCount = cell.getAnimals().size();
            int maxCapacity = 0;
            for (Animal animal : cell.getAnimals()) {
                maxCapacity += Settings.ANIMAL_DATA[animal.getType()].maxPerCell;
            }
            return animalCount / (double) maxCapacity;
        }
    }

    public static class Horse extends Herbivore {
        public Horse(int x, int y) {
            super(x, y, Settings.HORSE, 25);
        }

        @Override
        protected Animal createBaby() {
            return new Horse(x, y);
        }
    }

    public static class Deer extends Herbivore {
        public Deer(int x, int y) {
            super(x, y, Settings.DEER, 20);
        }

        @Override
        protected Animal createBaby() {
            return new Deer(x, y);
        }
    }

    public static class Rabbit extends Herbivore {
        public Rabbit(int x, int y) {
            super(x, y, Settings.RABBIT, 9);
        }

        @Override
        protected Animal createBaby() {
            return new Rabbit(x, y);
        }

        @Override
        public void reproduce(Cell cell) {
            if (!isAlive || !canReproduce) return;

            if (satiety >= Settings.ANIMAL_DATA[type].foodRequired * 1.3) {
                if (random.nextInt(100) < 10) {
                    super.reproduce(cell);
                }
            }
        }
    }

    public static class Mouse extends Herbivore {
        public Mouse(int x, int y) {
            super(x, y, Settings.MOUSE, 5);
        }

        @Override
        protected Animal createBaby() {
            return new Mouse(x, y);
        }

        @Override
        protected void eatSpecialFood(Cell cell) {
            if (satiety < Settings.ANIMAL_DATA[type].foodRequired * 0.8) {
                for (Animal animal : cell.getAnimals()) {
                    if (animal.getType() == Settings.CATERPILLAR && animal.isAlive()) {
                        int probability = Settings.EAT_PROBABILITY[type][Settings.CATERPILLAR];
                        if (random.nextInt(100) < probability) {
                            satiety += 0.5;
                            animal.isAlive = false;
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void reproduce(Cell cell) {
            if (!isAlive || !canReproduce) return;

            if (satiety >= Settings.ANIMAL_DATA[type].foodRequired * 1.5) {
                if (random.nextInt(100) < 5) {
                    List<Animal> animals = cell.getAnimals();
                    int sameTypeCount = 0;
                    Animal partner = null;

                    for (Animal animal : animals) {
                        if (animal.getType() == type && animal != this && animal.isAlive()
                                && ((Mouse) animal).canReproduce) {
                            sameTypeCount++;
                            partner = animal;
                        }
                    }

                    if (sameTypeCount >= 2 && partner != null) {
                        long miceCount = animals.stream()
                                .filter(a -> a.getType() == Settings.MOUSE)
                                .count();

                        if (miceCount < Settings.ANIMAL_DATA[type].maxPerCell * 0.2) {
                            Animal baby = createBaby();
                            if (baby != null) {
                                cell.addAnimal(baby);
                                satiety *= 0.5;
                                ((Mouse) partner).satiety *= 0.5;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void decreaseSatiety() {
            satiety -= Settings.ANIMAL_DATA[type].foodRequired * 0.2;
            if (satiety <= 0) {
                isAlive = false;
            }
        }
    }

    public static class Goat extends Herbivore {
        public Goat(int x, int y) {
            super(x, y, Settings.GOAT, 15);
        }

        @Override
        protected Animal createBaby() {
            return new Goat(x, y);
        }
    }

    public static class Sheep extends Herbivore {
        public Sheep(int x, int y) {
            super(x, y, Settings.SHEEP, 12);
        }

        @Override
        protected Animal createBaby() {
            return new Sheep(x, y);
        }
    }

    public static class Boar extends Herbivore {
        public Boar(int x, int y) {
            super(x, y, Settings.BOAR, 15);
        }

        @Override
        protected Animal createBaby() {
            return new Boar(x, y);
        }

        @Override
        protected void eatSpecialFood(Cell cell) {
            if (satiety < Settings.ANIMAL_DATA[type].foodRequired * 0.6) {
                for (Animal animal : cell.getAnimals()) {
                    if (animal.getType() == Settings.MOUSE && animal.isAlive()) {
                        int probability = Settings.EAT_PROBABILITY[type][Settings.MOUSE];
                        if (random.nextInt(100) < probability) {
                            satiety += 2;
                            animal.isAlive = false;
                            break;
                        }
                    }
                }
            }
        }
    }

    public static class Buffalo extends Herbivore {
        public Buffalo(int x, int y) {
            super(x, y, Settings.BUFFALO, 20);
        }

        @Override
        protected Animal createBaby() {
            return new Buffalo(x, y);
        }
    }

    public static class Duck extends Herbivore {
        public Duck(int x, int y) {
            super(x, y, Settings.DUCK, 10);
        }

        @Override
        protected Animal createBaby() {
            return new Duck(x, y);
        }

        @Override
        protected void eatSpecialFood(Cell cell) {
            if (satiety < Settings.ANIMAL_DATA[type].foodRequired * 0.7) {
                for (Animal animal : cell.getAnimals()) {
                    if (animal.getType() == Settings.CATERPILLAR && animal.isAlive()) {
                        int probability = Settings.EAT_PROBABILITY[type][Settings.CATERPILLAR];
                        if (random.nextInt(100) < probability) {
                            satiety += 0.3;
                            animal.isAlive = false;
                            break;
                        }
                    }
                }
            }
        }
    }

    public static class Caterpillar extends Herbivore {
        public Caterpillar(int x, int y) {
            super(x, y, Settings.CATERPILLAR, 2);
        }

        @Override
        protected Animal createBaby() {
            return new Caterpillar(x, y);
        }

        @Override
        public void eat(Cell cell) {
            if (cell.getPlantCount() > 0 && random.nextInt(100) < 30) {
                if (cell.eatPlant()) {
                    satiety += 0.1;
                }
            }
        }

        @Override
        public void move(Cell[][] island) {
            if (random.nextInt(100) < 5) {
                super.move(island);
            }
        }

        @Override
        public void decreaseSatiety() {
            satiety -= 0.001;
            if (satiety <= 0) {
                isAlive = false;
            }
        }

        @Override
        public void age() {
            super.age();
            if (age > maxAge) {
                isAlive = false;
            }
        }
    }
}