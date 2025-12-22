package model;

import config.Settings;
import model.animals.*;
import model.animals.Predators.*;
import model.animals.Herbivores.*;
import utils.Statistics;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;

public class Island {
    private Cell[][] cells;
    private ScheduledExecutorService mainScheduler;
    private ExecutorService animalExecutor;
    private Random random;
    private Statistics statistics;
    private int tickCount = 0;
    private boolean simulationRunning = true;

    public Island() {
        cells = new Cell[Settings.ISLAND_HEIGHT][Settings.ISLAND_WIDTH];
        random = new Random();
        mainScheduler = Executors.newScheduledThreadPool(4);
        animalExecutor = new ThreadPoolExecutor(
                10, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
        );
        statistics = new Statistics(this);

        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                cells[y][x] = new Cell(x, y);
            }
        }

        initializeAllAnimals();
    }

    private void initializeAllAnimals() {
        initializePredators();
        initializeHerbivores();
    }

    private void initializePredators() {
        createAnimalsOfType(Settings.WOLF, 20);
        createAnimalsOfType(Settings.BOA, 15);
        createAnimalsOfType(Settings.FOX, 25);
        createAnimalsOfType(Settings.BEAR, 8);
        createAnimalsOfType(Settings.EAGLE, 18);
    }

    private void initializeHerbivores() {
        createAnimalsOfType(Settings.HORSE, 30);
        createAnimalsOfType(Settings.DEER, 25);
        createAnimalsOfType(Settings.RABBIT, 50);
        createAnimalsOfType(Settings.MOUSE, 100);
        createAnimalsOfType(Settings.GOAT, 35);
        createAnimalsOfType(Settings.SHEEP, 35);
        createAnimalsOfType(Settings.BOAR, 20);
        createAnimalsOfType(Settings.BUFFALO, 12);
        createAnimalsOfType(Settings.DUCK, 40);
        createAnimalsOfType(Settings.CATERPILLAR, 150);
    }

    private void createAnimalsOfType(int type, int count) {
        int created = 0;
        int attempts = 0;
        int maxAttempts = count * 10;

        while (created < count && attempts < maxAttempts) {
            int x = random.nextInt(Settings.ISLAND_WIDTH);
            int y = random.nextInt(Settings.ISLAND_HEIGHT);

            Cell cell = cells[y][x];
            if (cell.canAddAnimal(type)) {
                Animal animal = createAnimal(type, x, y);
                if (animal != null) {
                    cell.addAnimal(animal);
                    created++;
                }
            }
            attempts++;
        }
    }

    private Animal createAnimal(int type, int x, int y) {
        switch(type) {
            case Settings.WOLF: return new Predators.Wolf(x, y);
            case Settings.BOA: return new Predators.Boa(x, y);
            case Settings.FOX: return new Predators.Fox(x, y);
            case Settings.BEAR: return new Predators.Bear(x, y);
            case Settings.EAGLE: return new Predators.Eagle(x, y);
            case Settings.HORSE: return new Herbivores.Horse(x, y);
            case Settings.DEER: return new Herbivores.Deer(x, y);
            case Settings.RABBIT: return new Herbivores.Rabbit(x, y);
            case Settings.MOUSE: return new Herbivores.Mouse(x, y);
            case Settings.GOAT: return new Herbivores.Goat(x, y);
            case Settings.SHEEP: return new Herbivores.Sheep(x, y);
            case Settings.BOAR: return new Herbivores.Boar(x, y);
            case Settings.BUFFALO: return new Herbivores.Buffalo(x, y);
            case Settings.DUCK: return new Herbivores.Duck(x, y);
            case Settings.CATERPILLAR: return new Herbivores.Caterpillar(x, y);
            default: return null;
        }
    }

    public void startSimulation() {
        System.out.println("🚀 ЗАПУСК ОСТРОВА!");

        mainScheduler.scheduleAtFixedRate(this::growPlantsTask, 0,
                Settings.TICK_DURATION_MS, TimeUnit.MILLISECONDS);

        mainScheduler.scheduleAtFixedRate(this::animalLifeCycleTask, 0,
                Settings.TICK_DURATION_MS, TimeUnit.MILLISECONDS);

        mainScheduler.scheduleAtFixedRate(this::cleanupTask, 0,
                Settings.TICK_DURATION_MS * 2, TimeUnit.MILLISECONDS);

        mainScheduler.scheduleAtFixedRate(this::statisticsTask, 0,
                Settings.TICK_DURATION_MS * 3, TimeUnit.MILLISECONDS);
    }

    private void growPlantsTask() {
        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                cells[y][x].growPlants();
                if (random.nextInt(100) < Settings.PLANT_GROWTH_RATE) {
                    cells[y][x].addPlant();
                }
            }
        }
    }

    private void animalLifeCycleTask() {
        tickCount++;

        if (tickCount % 10 == 0) {
            checkPopulationBalance();
        }

        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                final int cellY = y;
                final int cellX = x;

                animalExecutor.submit(() -> {
                    Cell cell = cells[cellY][cellX];

                    for (Animal animal : cell.getAnimals()) {
                        if (animal.isAlive() && simulationRunning) {
                            try {
                                animal.eat(cell);
                                animal.move(cells);
                                animal.reproduce(cell);
                                animal.decreaseSatiety();
                                animal.age();
                            } catch (Exception e) {
                                System.err.println("Ошибка при обработке животного: " + e.getMessage());
                            }
                        }
                    }
                });
            }
        }
    }

    private void checkPopulationBalance() {
        Map<Integer, Integer> counts = statistics.countAllAnimals();

        int miceCount = counts.getOrDefault(Settings.MOUSE, 0);
        int foxesCount = counts.getOrDefault(Settings.FOX, 0);

        if (miceCount > 100 && foxesCount < 10) {
            for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
                for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                    Cell cell = cells[y][x];
                    for (Animal animal : cell.getAnimals()) {
                        if (animal.getType() == Settings.FOX && animal.isAlive()) {
                            animal.satiety = Math.min(
                                    animal.satiety * 1.5,
                                    Settings.ANIMAL_DATA[Settings.FOX].foodRequired * 2
                            );
                        }
                    }
                }
            }
        }
    }

    private void cleanupTask() {
        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                cells[y][x].clearDeadAnimals();
                cells[y][x].clearDeadPlants();
            }
        }
    }

    private void statisticsTask() {
        System.out.println("\n📊 ТАКТ " + tickCount);
        System.out.println("-".repeat(30));

        boolean hasLivingAnimals = statistics.hasLivingAnimals();

        if (!hasLivingAnimals) {
            System.out.println("❌ Все животные погибли. Симуляция завершена.");
            stopSimulation();
            return;
        }

        statistics.printShortStatistics();

        if (tickCount % 10 == 0) {
            statistics.printDetailedStatistics();
        }

        if (tickCount % 20 == 0) {
            statistics.printFullStatistics();
        }

        if (tickCount % 30 == 0) {
            statistics.printVisualMap();
        }

        if (tickCount % 50 == 0) {
            statistics.printPopulationBalance();
        }
    }

    public void stopSimulation() {
        simulationRunning = false;
        System.out.println("\n⏹️ ОСТАНОВКА СИМУЛЯЦИИ...");

        mainScheduler.shutdown();
        animalExecutor.shutdown();

        try {
            if (!mainScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                mainScheduler.shutdownNow();
            }
            if (!animalExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                animalExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            mainScheduler.shutdownNow();
            animalExecutor.shutdownNow();
        }

        System.out.println("\n📋 ФИНАЛЬНАЯ СТАТИСТИКА:");
        System.out.println("=".repeat(50));
        statistics.printFullStatistics();
        statistics.printVisualMap();
        System.out.println("\n✅ Симуляция завершена за " + tickCount + " тактов.");
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getTickCount() {
        return tickCount;
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }
}