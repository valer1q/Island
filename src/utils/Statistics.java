package utils;

import model.Island;
import model.Cell;
import config.Settings;
import model.animals.Animal;
import java.util.HashMap;
import java.util.Map;

public class Statistics {
    private Island island;

    public Statistics(Island island) {
        this.island = island;
    }

    public boolean hasLivingAnimals() {
        Cell[][] cells = island.getCells();

        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                Cell cell = cells[y][x];
                for (Animal animal : cell.getAnimals()) {
                    if (animal.isAlive()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void printShortStatistics() {
        Map<Integer, Integer> animalCounts = new HashMap<>();
        int totalPlants = 0;
        int totalAnimals = 0;

        Cell[][] cells = island.getCells();

        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                Cell cell = cells[y][x];
                totalPlants += cell.getPlantCount();

                for (Animal animal : cell.getAnimals()) {
                    if (animal.isAlive()) {
                        int type = animal.getType();
                        animalCounts.put(type, animalCounts.getOrDefault(type, 0) + 1);
                        totalAnimals++;
                    }
                }
            }
        }

        System.out.printf("🌿 Растения: %d | ", totalPlants);
        System.out.printf("🐾 Животные: %d%n", totalAnimals);
        System.out.printf("🐺 Хищники: %d | ",
                animalCounts.getOrDefault(Settings.WOLF, 0) +
                        animalCounts.getOrDefault(Settings.BOA, 0) +
                        animalCounts.getOrDefault(Settings.FOX, 0) +
                        animalCounts.getOrDefault(Settings.BEAR, 0) +
                        animalCounts.getOrDefault(Settings.EAGLE, 0));
        System.out.printf("🌱 Травоядные: %d%n",
                animalCounts.getOrDefault(Settings.HORSE, 0) +
                        animalCounts.getOrDefault(Settings.DEER, 0) +
                        animalCounts.getOrDefault(Settings.RABBIT, 0) +
                        animalCounts.getOrDefault(Settings.MOUSE, 0) +
                        animalCounts.getOrDefault(Settings.GOAT, 0) +
                        animalCounts.getOrDefault(Settings.SHEEP, 0) +
                        animalCounts.getOrDefault(Settings.BOAR, 0) +
                        animalCounts.getOrDefault(Settings.BUFFALO, 0) +
                        animalCounts.getOrDefault(Settings.DUCK, 0) +
                        animalCounts.getOrDefault(Settings.CATERPILLAR, 0));
    }

    public void printDetailedStatistics() {
        Map<Integer, Integer> animalCounts = countAllAnimals();

        System.out.println("\n📈 ДЕТАЛЬНАЯ СТАТИСТИКА:");
        System.out.println("-".repeat(40));

        System.out.println("ХИЩНИКИ:");
        System.out.printf("  🐺 Волки: %d | 🐍 Удавы: %d | 🦊 Лисы: %d%n",
                animalCounts.getOrDefault(Settings.WOLF, 0),
                animalCounts.getOrDefault(Settings.BOA, 0),
                animalCounts.getOrDefault(Settings.FOX, 0));
        System.out.printf("  🐻 Медведи: %d | 🦅 Орлы: %d%n",
                animalCounts.getOrDefault(Settings.BEAR, 0),
                animalCounts.getOrDefault(Settings.EAGLE, 0));

        System.out.println("\nТРАВОЯДНЫЕ:");
        System.out.printf("  🐎 Лошади: %d | 🦌 Олени: %d | 🐇 Кролики: %d%n",
                animalCounts.getOrDefault(Settings.HORSE, 0),
                animalCounts.getOrDefault(Settings.DEER, 0),
                animalCounts.getOrDefault(Settings.RABBIT, 0));
        System.out.printf("  🐁 Мыши: %d | 🐐 Козы: %d | 🐑 Овцы: %d%n",
                animalCounts.getOrDefault(Settings.MOUSE, 0),
                animalCounts.getOrDefault(Settings.GOAT, 0),
                animalCounts.getOrDefault(Settings.SHEEP, 0));
        System.out.printf("  🐗 Кабаны: %d | 🐃 Буйволы: %d | 🦆 Утки: %d | 🐛 Гусеницы: %d%n",
                animalCounts.getOrDefault(Settings.BOAR, 0),
                animalCounts.getOrDefault(Settings.BUFFALO, 0),
                animalCounts.getOrDefault(Settings.DUCK, 0),
                animalCounts.getOrDefault(Settings.CATERPILLAR, 0));
    }

    public void printFullStatistics() {
        Map<Integer, Integer> animalCounts = countAllAnimals();
        int totalPlants = countAllPlants();
        int totalAnimals = animalCounts.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ПОЛНАЯ СТАТИСТИКА ОСТРОВА - ТАКТ " + island.getTickCount());
        System.out.println("=".repeat(60));
        System.out.printf("🌿 Всего растений: %d%n", totalPlants);
        System.out.printf("🐾 Всего животных: %d%n", totalAnimals);
        System.out.println("-".repeat(60));

        System.out.println("ХИЩНИКИ (5 видов):");
        printAnimalStat("Волк", "🐺", Settings.WOLF, animalCounts);
        printAnimalStat("Удав", "🐍", Settings.BOA, animalCounts);
        printAnimalStat("Лиса", "🦊", Settings.FOX, animalCounts);
        printAnimalStat("Медведь", "🐻", Settings.BEAR, animalCounts);
        printAnimalStat("Орел", "🦅", Settings.EAGLE, animalCounts);

        System.out.println("\nТРАВОЯДНЫЕ (10 видов):");
        printAnimalStat("Лошадь", "🐎", Settings.HORSE, animalCounts);
        printAnimalStat("Олень", "🦌", Settings.DEER, animalCounts);
        printAnimalStat("Кролик", "🐇", Settings.RABBIT, animalCounts);
        printAnimalStat("Мышь", "🐁", Settings.MOUSE, animalCounts);
        printAnimalStat("Коза", "🐐", Settings.GOAT, animalCounts);
        printAnimalStat("Овца", "🐑", Settings.SHEEP, animalCounts);
        printAnimalStat("Кабан", "🐗", Settings.BOAR, animalCounts);
        printAnimalStat("Буйвол", "🐃", Settings.BUFFALO, animalCounts);
        printAnimalStat("Утка", "🦆", Settings.DUCK, animalCounts);
        printAnimalStat("Гусеница", "🐛", Settings.CATERPILLAR, animalCounts);

        System.out.println("=".repeat(60));
    }

    private void printAnimalStat(String name, String symbol, int type, Map<Integer, Integer> counts) {
        int count = counts.getOrDefault(type, 0);
        System.out.printf("  %s %s: %d (макс. %d на клетку)%n",
                symbol, name, count, Settings.ANIMAL_DATA[type].maxPerCell);
    }

    public void printVisualMap() {
        Cell[][] cells = island.getCells();

        System.out.println("\n🗺️ КАРТА ОСТРОВА:");
        System.out.println("=".repeat(Settings.ISLAND_WIDTH * 2 + 2));

        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            System.out.print("|");
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                System.out.print(cells[y][x].getCellSymbol());
            }
            System.out.println("|");
        }
        System.out.println("=".repeat(Settings.ISLAND_WIDTH * 2 + 2));

        System.out.println("\nЛЕГЕНДА:");
        System.out.println("🐺 🐍 🦊 🐻 🦅 - хищники");
        System.out.println("🐎 🦌 🐇 🐁 🐐 🐑 🐗 🐃 🦆 🐛 - травоядные");
        System.out.println("🌱 🌿 ☘️ 🍀 🌳 - растения (возраст)");
        System.out.println("· - пустая клетка");
    }

    public void printPopulationBalance() {
        Map<Integer, Integer> counts = countAllAnimals();

        int predators = counts.getOrDefault(Settings.WOLF, 0) +
                counts.getOrDefault(Settings.BOA, 0) +
                counts.getOrDefault(Settings.FOX, 0) +
                counts.getOrDefault(Settings.BEAR, 0) +
                counts.getOrDefault(Settings.EAGLE, 0);

        int herbivores = counts.getOrDefault(Settings.HORSE, 0) +
                counts.getOrDefault(Settings.DEER, 0) +
                counts.getOrDefault(Settings.RABBIT, 0) +
                counts.getOrDefault(Settings.MOUSE, 0) +
                counts.getOrDefault(Settings.GOAT, 0) +
                counts.getOrDefault(Settings.SHEEP, 0) +
                counts.getOrDefault(Settings.BOAR, 0) +
                counts.getOrDefault(Settings.BUFFALO, 0) +
                counts.getOrDefault(Settings.DUCK, 0) +
                counts.getOrDefault(Settings.CATERPILLAR, 0);

        double ratio = predators > 0 ? (double) herbivores / predators : Double.MAX_VALUE;

        System.out.println("\n⚖️  БАЛАНС ПОПУЛЯЦИЙ:");
        System.out.println("-".repeat(40));
        System.out.printf("Хищники: %d | Травоядные: %d | Соотношение: %.1f:1%n",
                predators, herbivores, ratio);

        if (ratio > 50) {
            System.out.println("⚠️  Слишком много травоядных! Хищники не справляются.");
        } else if (ratio < 5) {
            System.out.println("⚠️  Слишком много хищников! Травоядные могут исчезнуть.");
        } else {
            System.out.println("✅ Баланс популяций в норме.");
        }
    }

    public Map<Integer, Integer> countAllAnimals() {
        Map<Integer, Integer> counts = new HashMap<>();
        Cell[][] cells = island.getCells();

        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                Cell cell = cells[y][x];
                for (Animal animal : cell.getAnimals()) {
                    if (animal.isAlive()) {
                        int type = animal.getType();
                        counts.put(type, counts.getOrDefault(type, 0) + 1);
                    }
                }
            }
        }
        return counts;
    }

    private int countAllPlants() {
        int total = 0;
        Cell[][] cells = island.getCells();

        for (int y = 0; y < Settings.ISLAND_HEIGHT; y++) {
            for (int x = 0; x < Settings.ISLAND_WIDTH; x++) {
                total += cells[y][x].getPlantCount();
            }
        }
        return total;
    }
}