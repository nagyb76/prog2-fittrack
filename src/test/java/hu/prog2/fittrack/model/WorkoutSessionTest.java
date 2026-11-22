package hu.prog2.fittrack.model;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutSessionTest {

    @BeforeEach
    void setUp() {
        WorkoutSession.resetNextId(1);
    }

    @Test
    @DisplayName("CardioWorkout creation and property calculation should be valid")
    void testCardioWorkoutCreation() throws InvalidWorkoutDataException {
        CardioWorkout cardio = new CardioWorkout("Run in Park", LocalDate.of(2026, 11, 20), 60, 10.0, 150);

        assertEquals("Cardio", cardio.getWorkoutType());
        assertEquals(10.0, cardio.getDistanceKm());
        assertEquals(150, cardio.getAverageHeartRate());
        assertEquals(10.0, cardio.getAverageSpeedKmH(), 0.001);
        assertEquals(9.8, cardio.getBaseMetValue());
        assertTrue(cardio.getSpecificDetails().contains("10.00 km"));
    }

    @Test
    @DisplayName("StrengthWorkout creation and tonnage calculation should be valid")
    void testStrengthWorkoutCreation() throws InvalidWorkoutDataException {
        StrengthWorkout strength = new StrengthWorkout("Leg Day", LocalDate.of(2026, 11, 19), 50, 4, 10, 100.0);

        assertEquals("Strength", strength.getWorkoutType());
        assertEquals(4, strength.getSets());
        assertEquals(10, strength.getReps());
        assertEquals(100.0, strength.getWeightKg());
        assertEquals(4000.0, strength.getTotalTonnageKg(), 0.001);
        assertEquals(6.0, strength.getBaseMetValue());
    }

    @Test
    @DisplayName("FlexibilityWorkout creation should be valid")
    void testFlexibilityWorkoutCreation() throws InvalidWorkoutDataException {
        FlexibilityWorkout flex = new FlexibilityWorkout("Morning Flow", LocalDate.of(2026, 11, 18), 30, "Spine", 4);

        assertEquals("Flexibility & Yoga", flex.getWorkoutType());
        assertEquals("Spine", flex.getFocusArea());
        assertEquals(4, flex.getIntensityLevel());
        assertEquals(4.5, flex.getBaseMetValue(), 0.001);
    }

    @Test
    @DisplayName("Invalid parameters should trigger InvalidWorkoutDataException")
    void testInvalidWorkoutDataExceptions() {
        LocalDate date = LocalDate.now();

        // Empty title
        assertThrows(InvalidWorkoutDataException.class, () ->
                new CardioWorkout("  ", date, 30, 5.0, 140));

        // Negative duration
        assertThrows(InvalidWorkoutDataException.class, () ->
                new CardioWorkout("Run", date, -5, 5.0, 140));

        // Zero distance
        assertThrows(InvalidWorkoutDataException.class, () ->
                new CardioWorkout("Run", date, 30, 0.0, 140));

        // Invalid sets
        assertThrows(InvalidWorkoutDataException.class, () ->
                new StrengthWorkout("Gym", date, 45, 0, 10, 50.0));

        // Invalid flexibility intensity
        assertThrows(InvalidWorkoutDataException.class, () ->
                new FlexibilityWorkout("Yoga", date, 45, "Core", 6));
    }

    @Test
    @DisplayName("Comparable should sort workouts by date descending (most recent first)")
    void testComparableNaturalOrdering() throws InvalidWorkoutDataException {
        CardioWorkout w1 = new CardioWorkout("Older", LocalDate.of(2026, 11, 10), 30, 5.0, 140);
        CardioWorkout w2 = new CardioWorkout("Newer", LocalDate.of(2026, 11, 20), 30, 5.0, 140);

        assertTrue(w1.compareTo(w2) > 0, "Older workout should come after newer workout in natural order");
        assertTrue(w2.compareTo(w1) < 0, "Newer workout should come before older workout in natural order");
    }

    @Test
    @DisplayName("UserProfile validation should accept valid parameters and reject invalid ones")
    void testUserProfileValidation() throws InvalidWorkoutDataException {
        UserProfile profile = new UserProfile("Anna", 28, 62.5, Gender.FEMALE, 58);
        assertEquals("Anna", profile.getName());
        assertEquals(28, profile.getAge());
        assertEquals(62.5, profile.getWeightKg());

        // Negative age
        assertThrows(InvalidWorkoutDataException.class, () ->
                new UserProfile("Anna", -5, 62.5, Gender.FEMALE, 58));

        // Weight out of bounds
        assertThrows(InvalidWorkoutDataException.class, () ->
                new UserProfile("Anna", 28, 15.0, Gender.FEMALE, 58));
    }
}
