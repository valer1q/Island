package model;

import config.Settings;
import model.animals.Animal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CellTest {

    @Test
    void constructor_setsCoordinates_andInitialPlantsRange() {
        Cell cell = new Cell(3, 7);
        assertEquals(3, cell.getX());
        assertEquals(7, cell.getY());

        // initializePlants(): nextInt(10)+5 => [5..14]
        int plants = cell.getPlants().size();
        assertTrue(plants >= 5 && plants <= 14, "plants=" + plants);
    }

    @Test
    void addPlants_doesNotExceedMaxPerCell() {
        Cell cell = new Cell(0, 0);
        cell.getPlants().clear();

        int max = Settings.ANIMAL_DATA[Settings.PLANT].maxPerCell;
        cell.addPlants(max + 1000);

        assertEquals(max, cell.getPlants().size());
    }

    @Test
    void eatPlant_returnsFalse_whenNoAlivePlants() {
        Cell cell = new Cell(0, 0);
        cell.getPlants().clear();

        Plant dead = Mockito.mock(Plant.class);
        when(dead.isAlive()).thenReturn(false);

        cell.getPlants().add(dead);

        assertFalse(cell.eatPlant());
        verify(dead, never()).eat();
    }

    @Test
    void eatPlant_returnsTrue_andCallsEat_whenAlivePlantExists() {
        Cell cell = new Cell(0, 0);
        cell.getPlants().clear();

        Plant alive = Mockito.mock(Plant.class);
        when(alive.isAlive()).thenReturn(true);

        cell.getPlants().add(alive);

        assertTrue(cell.eatPlant());
        verify(alive, times(1)).eat();
    }

    @Test
    void clearDeadPlants_removesDeadOnly() {
        Cell cell = new Cell(0, 0);
        cell.getPlants().clear();

        Plant alive = Mockito.mock(Plant.class);
        when(alive.isAlive()).thenReturn(true);

        Plant dead = Mockito.mock(Plant.class);
        when(dead.isAlive()).thenReturn(false);

        cell.getPlants().add(alive);
        cell.getPlants().add(dead);

        cell.clearDeadPlants();

        assertEquals(1, cell.getPlants().size());
        assertSame(alive, cell.getPlants().get(0));
    }

    @Test
    void clearDeadAnimals_removesDeadOnly() {
        Cell cell = new Cell(0, 0);
        cell.getAnimals().clear();

        Animal alive = Mockito.mock(Animal.class);
        when(alive.isAlive()).thenReturn(true);

        Animal dead = Mockito.mock(Animal.class);
        when(dead.isAlive()).thenReturn(false);

        cell.getAnimals().add(alive);
        cell.getAnimals().add(dead);

        cell.clearDeadAnimals();

        assertEquals(1, cell.getAnimals().size());
        assertSame(alive, cell.getAnimals().get(0));
    }

    @Test
    void getCellSymbol_prefersAnimalSymbol() {
        Cell cell = new Cell(0, 0);
        cell.getAnimals().clear();

        Animal wolf = Mockito.mock(Animal.class);
        when(wolf.getSymbol()).thenReturn("🐺");
        when(wolf.isAlive()).thenReturn(true);

        cell.getAnimals().add(wolf);

        assertEquals("🐺", cell.getCellSymbol());
    }

    @Test
    void getCellSymbol_whenNoAnimals_usesHighestGrowthPlantSymbol_orDot() {
        Cell cell = new Cell(0, 0);
        cell.getAnimals().clear();
        cell.getPlants().clear();

        Plant p1 = Mockito.mock(Plant.class);
        when(p1.isAlive()).thenReturn(true);
        when(p1.getGrowthStage()).thenReturn(1);
        when(p1.getSymbol()).thenReturn("🌱");

        Plant p2 = Mockito.mock(Plant.class);
        when(p2.isAlive()).thenReturn(true);
        when(p2.getGrowthStage()).thenReturn(3);
        when(p2.getSymbol()).thenReturn("🌿");

        cell.getPlants().add(p1);
        cell.getPlants().add(p2);

        assertEquals("🌿", cell.getCellSymbol());

        // если все мёртвые — точка
        when(p1.isAlive()).thenReturn(false);
        when(p2.isAlive()).thenReturn(false);
        assertEquals("·", cell.getCellSymbol());
    }

    @Test
    void canAddAnimal_countsOnlyAlive_andRespectsMaxPerCell() {
        Cell cell = new Cell(0, 0);
        cell.getAnimals().clear();

        int type = Settings.WOLF;
        int max = Settings.ANIMAL_DATA[type].maxPerCell;

        // max живых волков
        for (int i = 0; i < max; i++) {
            Animal a = Mockito.mock(Animal.class);
            when(a.getType()).thenReturn(type);
            when(a.isAlive()).thenReturn(true);
            cell.getAnimals().add(a);
        }

        // + один мёртвый не должен влиять
        Animal dead = Mockito.mock(Animal.class);
        when(dead.getType()).thenReturn(type);
        when(dead.isAlive()).thenReturn(false);
        cell.getAnimals().add(dead);

        assertFalse(cell.canAddAnimal(type));
    }

    @Test
    void growPlants_callsGrowOnAlivePlants_andDoesNotRandomlyAdd_whenForced() throws Exception {
        Cell cell = new Cell(0, 0);
        cell.getPlants().clear();

        Plant p = Mockito.mock(Plant.class);
        when(p.isAlive()).thenReturn(true);
        cell.getPlants().add(p);

        // подменяем Random так, чтобы nextInt(100) всегда возвращал 99 (не < 5)
        Field f = Cell.class.getDeclaredField("random");
        f.setAccessible(true);
        f.set(cell, new Random() {
            @Override public int nextInt(int bound) { return 99; }
        });

        int before = cell.getPlants().size();
        cell.growPlants();
        int after = cell.getPlants().size();

        assertEquals(before, after);
        verify(p, times(1)).grow();
    }
}
