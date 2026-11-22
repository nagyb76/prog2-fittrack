package hu.prog2.fittrack.strategy;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;
import hu.prog2.fittrack.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyTest {
    private UserProfile maleProfile;
    private UserProfile femaleProfile;
    private MetBasedStrategy metStrategy;
    private HeartRateStrategy heartRateStrategy;

    @BeforeEach
    void setUp() throws InvalidWorkoutDataException {
        maleProfile = new UserProfile("Gabor", 30, 80.0, Gender.MALE, 60);
        femaleProfile = new UserProfile("Kata", 25, 60.0, Gender.FEMALE, 62);
        metStrategy = new MetBasedStrategy();
        heartRateStrategy = new HeartRateStrategy();
    }

    @Test
    @DisplayName("MetBasedStrategy calculates calories accurately based on MET, weight and duration")
    void testMetBasedCalculation() throws InvalidWorkoutDataException {
        // Jogging: base MET = 7.0 (speed 8 km/h for 45 min = 6.0 km)
        CardioWorkout run = new CardioWorkout("Jog", LocalDate.now(), 60, 8.0, 0);

        // Calories = 7.0 * 80 kg * 1 hour = 560 kcal
        double calories = metStrategy.calculateCalories(run, maleProfile);
        assertEquals(560.0, calories, 1.0);
    }

    @Test
    @DisplayName("HeartRateStrategy utilizes heart rate when available")
    void testHeartRateStrategyWithHr() throws InvalidWorkoutDataException {
        CardioWorkout runWithHr = new CardioWorkout("Tempo Run", LocalDate.now(), 45, 7.5, 155);

        double hrCalories = heartRateStrategy.calculateCalories(runWithHr, maleProfile);
        double metCalories = metStrategy.calculateCalories(runWithHr, maleProfile);

        assertTrue(hrCalories > 0);
        assertTrue(metCalories > 0);
        // Both provide realistic estimates within exercise ranges
        assertTrue(hrCalories >= 300 && hrCalories <= 800);
    }

    @Test
    @DisplayName("HeartRateStrategy gracefully falls back to MET calculation when HR is 0")
    void testHeartRateStrategyFallback() throws InvalidWorkoutDataException {
        CardioWorkout runNoHr = new CardioWorkout("Easy Run", LocalDate.now(), 60, 8.0, 0);

        double hrCalories = heartRateStrategy.calculateCalories(runNoHr, maleProfile);
        double metCalories = metStrategy.calculateCalories(runNoHr, maleProfile);

        assertEquals(metCalories, hrCalories, 0.001,
                "HeartRateStrategy should fall back to MET calculation when HR is not recorded (0)");
    }

    @Test
    @DisplayName("WorkoutSession delegates calculation correctly to dynamic Strategy")
    void testWorkoutSessionDelegation() throws InvalidWorkoutDataException {
        StrengthWorkout strength = new StrengthWorkout("Deadlifts", LocalDate.now(), 60, 5, 5, 120.0);

        double viaSession = strength.calculateCalories(maleProfile, metStrategy);
        double viaDirectStrategy = metStrategy.calculateCalories(strength, maleProfile);

        assertEquals(viaDirectStrategy, viaSession, 0.001);
    }
}
