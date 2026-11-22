package hu.prog2.fittrack.persistence;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;
import hu.prog2.fittrack.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GsonWorkoutRepositoryTest {

    @Test
    @DisplayName("Polymorphic workout sessions and user profile should be saved and restored identically")
    void testSaveAndLoadPolymorphicData(@TempDir Path tempDir) throws IOException, InvalidWorkoutDataException {
        GsonWorkoutRepository repository = new GsonWorkoutRepository();
        Path testFile = tempDir.resolve("test_workouts.json");

        UserProfile profile = new UserProfile("Zoltan", 29, 82.5, Gender.MALE, 55);

        List<WorkoutSession> sessions = new ArrayList<>();
        sessions.add(new CardioWorkout(1, "Mountain Run", LocalDate.of(2026, 10, 5), 55, 8.2, 160));
        sessions.add(new StrengthWorkout(2, "Deadlift PR", LocalDate.of(2026, 10, 6), 45, 5, 3, 140.0));
        sessions.add(new FlexibilityWorkout(3, "Deep Stretch", LocalDate.of(2026, 10, 7), 25, "Hamstrings", 2));

        WorkoutData originalData = new WorkoutData(profile, sessions);

        // Save to file
        repository.save(originalData, testFile.toString());
        assertTrue(testFile.toFile().exists());

        // Load from file
        WorkoutData loadedData = repository.load(testFile.toString());
        assertNotNull(loadedData);

        // Verify profile
        assertEquals("Zoltan", loadedData.getProfile().getName());
        assertEquals(29, loadedData.getProfile().getAge());
        assertEquals(82.5, loadedData.getProfile().getWeightKg());
        assertEquals(Gender.MALE, loadedData.getProfile().getGender());

        // Verify sessions and correct polymorphic types
        List<WorkoutSession> loadedSessions = loadedData.getSessions();
        assertEquals(3, loadedSessions.size());

        assertTrue(loadedSessions.get(0) instanceof CardioWorkout);
        CardioWorkout loadedCardio = (CardioWorkout) loadedSessions.get(0);
        assertEquals("Mountain Run", loadedCardio.getTitle());
        assertEquals(8.2, loadedCardio.getDistanceKm());
        assertEquals(160, loadedCardio.getAverageHeartRate());

        assertTrue(loadedSessions.get(1) instanceof StrengthWorkout);
        StrengthWorkout loadedStrength = (StrengthWorkout) loadedSessions.get(1);
        assertEquals(140.0, loadedStrength.getWeightKg());
        assertEquals(5, loadedStrength.getSets());

        assertTrue(loadedSessions.get(2) instanceof FlexibilityWorkout);
        FlexibilityWorkout loadedFlex = (FlexibilityWorkout) loadedSessions.get(2);
        assertEquals("Hamstrings", loadedFlex.getFocusArea());
        assertEquals(2, loadedFlex.getIntensityLevel());
    }
}
