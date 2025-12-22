package model.animals;

import config.Settings;
import model.Cell;
import java.util.List;

public class Predators {

    public static abstract class Predator extends Animal {

        public Predator(int x, int y, int type, int maxAge) {
            super(x, y, type, maxAge);
        }

        @Override
        public void eat(Cell cell) {
            if (satiety >= Settings.ANIMAL_DATA[type].foodRequired * 1.5) {
                return;
            }

            List<Animal> animals = cell.getAnimals();
            boolean hasEaten = false;

            for (Animal prey : animals) {
                if (prey == this || !prey.isAlive()) continue;

                int probability = Settings.EAT_PROBABILITY[type][prey.getType()];
                if (probability > 50 && random.nextInt(100) < probability) {
                    double preyWeight = Settings.ANIMAL_DATA[prey.getType()].weight;
                    satiety += preyWeight * 0.7;
                    prey.isAlive = false;
                    hasEaten = true;
                    break;
                }
            }

            if (!hasEaten) {
                for (Animal prey : animals) {
                    if (prey == this || !prey.isAlive()) continue;

                    int probability = Settings.EAT_PROBABILITY[type][prey.getType()];
                    if (probability > 0 && random.nextInt(100) < probability) {
                        double preyWeight = Settings.ANIMAL_DATA[prey.getType()].weight;
                        satiety += preyWeight * 0.7;
                        prey.isAlive = false;
                        hasEaten = true;
                        break;
                    }
                }
            }

            if (!hasEaten && satiety < Settings.ANIMAL_DATA[type].foodRequired) {
                satiety -= Settings.ANIMAL_DATA[type].foodRequired * 0.1;
            }
        }

        @Override
        public void move(Cell[][] island) {
            if (!isAlive) return;

            int hungerFactor = satiety < Settings.ANIMAL_DATA[type].foodRequired ? 2 : 1;
            int maxSpeed = Settings.ANIMAL_DATA[type].maxSpeed * hungerFactor;

            int bestX = x;
            int bestY = y;
            double bestFoodScore = 0;

            for (int dy = -maxSpeed; dy <= maxSpeed; dy++) {
                for (int dx = -maxSpeed; dx <= maxSpeed; dx++) {
                    int checkY = y + dy;
                    int checkX = x + dx;

                    if (checkY >= 0 && checkY < Settings.ISLAND_HEIGHT &&
                            checkX >= 0 && checkX < Settings.ISLAND_WIDTH) {

                        double foodScore = calculateFoodScore(island[checkY][checkX]);
                        if (foodScore > bestFoodScore) {
                            bestFoodScore = foodScore;
                            bestX = checkX;
                            bestY = checkY;
                        }
                    }
                }
            }

            if (bestFoodScore > 0) {
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

        protected double calculateFoodScore(Cell cell) {
            double score = 0;
            for (Animal animal : cell.getAnimals()) {
                if (animal.isAlive() && animal != this) {
                    int probability = Settings.EAT_PROBABILITY[type][animal.getType()];
                    score += probability / 100.0;
                }
            }
            return score;
        }

        @Override
        public void reproduce(Cell cell) {
            if (!isAlive || !canReproduce) return;

            if (satiety >= Settings.ANIMAL_DATA[type].foodRequired * 1.3) {
                if (random.nextInt(100) < 5) {
                    List<Animal> animals = cell.getAnimals();
                    int sameTypeCount = 0;
                    Animal partner = null;

                    for (Animal animal : animals) {
                        if (animal.getType() == type && animal != this && animal.isAlive()
                                && ((Predator) animal).canReproduce) {
                            sameTypeCount++;
                            partner = animal;
                        }
                    }

                    if (sameTypeCount >= 1 && partner != null &&
                            animals.size() < Settings.ANIMAL_DATA[type].maxPerCell) {
                        Animal baby = createBaby();
                        if (baby != null) {
                            cell.addAnimal(baby);
                            satiety *= 0.6;
                            ((Predator) partner).satiety *= 0.6;
                        }
                    }
                }
            }
        }

        protected abstract Animal createBaby();
    }

    public static class Wolf extends Predator {
        public Wolf(int x, int y) {
            super(x, y, Settings.WOLF, 10);
        }

        @Override
        protected Animal createBaby() {
            return new Wolf(x, y);
        }
    }

    public static class Boa extends Predator {
        public Boa(int x, int y) {
            super(x, y, Settings.BOA, 15);
        }

        @Override
        protected Animal createBaby() {
            return new Boa(x, y);
        }

        @Override
        public void eat(Cell cell) {
            if (random.nextInt(100) < 30) {
                super.eat(cell);
            }
        }
    }

    public static class Fox extends Predator {
        public Fox(int x, int y) {
            super(x, y, Settings.FOX, 8);
        }

        @Override
        protected Animal createBaby() {
            return new Fox(x, y);
        }

        @Override
        protected double calculateFoodScore(Cell cell) {
            double score = 0;
            for (Animal animal : cell.getAnimals()) {
                if (animal.isAlive() && animal != this) {
                    if (animal.getType() == Settings.MOUSE) {
                        score += 2.0;
                    } else if (animal.getType() == Settings.RABBIT) {
                        score += 1.5;
                    }
                }
            }
            return score;
        }
    }

    public static class Bear extends Predator {
        public Bear(int x, int y) {
            super(x, y, Settings.BEAR, 25);
        }

        @Override
        protected Animal createBaby() {
            return new Bear(x, y);
        }

        @Override
        public void eat(Cell cell) {
            if (cell.getPlantCount() > 0 && random.nextInt(100) < 40) {
                if (cell.eatPlant()) {
                    satiety += 10;
                }
            } else {
                super.eat(cell);
            }
        }
    }

    public static class Eagle extends Predator {
        public Eagle(int x, int y) {
            super(x, y, Settings.EAGLE, 15);
        }

        @Override
        protected Animal createBaby() {
            return new Eagle(x, y);
        }

        @Override
        public void move(Cell[][] island) {
            if (!isAlive) return;

            int maxSpeed = Settings.ANIMAL_DATA[type].maxSpeed * 2;
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
}