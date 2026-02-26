package config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {

    @Test
    void islandDimensions_arePositive() {
        assertTrue(Settings.ISLAND_WIDTH > 0);
        assertTrue(Settings.ISLAND_HEIGHT > 0);
    }

    @Test
    void eatProbability_has16x16Shape() {
        assertEquals(16, Settings.EAT_PROBABILITY.length);
        for (int[] row : Settings.EAT_PROBABILITY) {
            assertEquals(16, row.length);
        }
    }

    @Test
    void eatProbability_diagonalIsMinusOne() {
        for (int i = 0; i < 16; i++) {
            assertEquals(-1, Settings.EAT_PROBABILITY[i][i]);
        }
    }

    @Test
    void eatProbability_valuesAreInRangeMinus1To100() {
        for (int[] row : Settings.EAT_PROBABILITY) {
            for (int v : row) {
                assertTrue(v >= -1 && v <= 100, "value=" + v);
            }
        }
    }

    @Test
    void animalData_has16Entries() {
        assertEquals(16, Settings.ANIMAL_DATA.length);
    }

    @Test
    void animalData_invariantsHold() {
        for (Settings.AnimalData d : Settings.ANIMAL_DATA) {
            assertTrue(d.weight > 0);
            assertTrue(d.maxPerCell > 0);
            assertTrue(d.maxSpeed >= 0);
            assertTrue(d.foodRequired >= 0);
        }
    }

    @Test
    void simulationTiming_isPositive() {
        assertTrue(Settings.TICK_DURATION_MS > 0);
        assertTrue(Settings.SIMULATION_DURATION_TICKS > 0);
        assertTrue(Settings.PLANT_GROWTH_RATE > 0);
    }
}
