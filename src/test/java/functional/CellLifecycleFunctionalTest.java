package functional;

import model.Cell;
import model.Plant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CellLifecycleFunctionalTest {

    @Test
    void plantIsEaten_thenRemoved_thenCellSymbolBecomesDot() {
        Cell cell = new Cell(0, 0);
        cell.getAnimals().clear();
        cell.getPlants().clear();

        Plant plant = Mockito.mock(Plant.class);

        // до "поедания" живое, после — мёртвое
        final boolean[] alive = { true };
        when(plant.isAlive()).thenAnswer(inv -> alive[0]);
        doAnswer(inv -> { alive[0] = false; return null; }).when(plant).eat();

        when(plant.getGrowthStage()).thenReturn(2);
        when(plant.getSymbol()).thenReturn("🌿");

        cell.getPlants().add(plant);

        assertEquals("🌿", cell.getCellSymbol());

        assertTrue(cell.eatPlant());
        cell.clearDeadPlants();

        assertTrue(cell.getPlants().isEmpty());
        assertEquals("·", cell.getCellSymbol());
    }
}
