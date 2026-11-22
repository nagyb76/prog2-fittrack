package hu.prog2.fittrack.service;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;
import hu.prog2.fittrack.exception.WorkoutNotFoundException;
import hu.prog2.fittrack.model.*;
import hu.prog2.fittrack.persistence.GsonWorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutTrackerServiceTest {
    private WorkoutTrackerService service;

    @BeforeEach
    void setUp() throws InvalidWorkoutDataException {
        WorkoutSession.resetNextId(1);
        service = new WorkoutTrackerService(new GsonWorkoutRepository());
        service.setUserProfile(new UserProfile("Tester", 24, 70.0, Gender.MALE, 60));

        service.addWorkout(new CardioWorkout("Morning Run", LocalDate.of(2026, 11, 10), 30, 5.0, 150));
        service.addWorkout(new StrengthWorkout("Bench Press", LocalDate.of(2026, 11, 15), 60, 5, 8, 80.0));
        service.addWorkout(new FlexibilityWorkout("Stretching", LocalDate.of(2026, 11, 20), 20, "Legs", 2));
    }

    @Test
    @DisplayName("Service should store and count added workouts")
    void testWorkoutCountAndRetrieval() throws WorkoutNotFoundException {
        assertEquals(3, service.getWorkoutCount());
        WorkoutSession session = service.findWorkoutById(1);
        assertNotNull(session);
        assertEquals("Morning Run", session.getTitle());
    }

    @Test
    @DisplayName("Removing existing workout succeeds; non-existing throws WorkoutNotFoundException")
    void testRemoveWorkout() throws WorkoutNotFoundException {
        WorkoutSession removed = service.removeWorkout(2);
        assertEquals(2, removed.getId());
        assertEquals(2, service.getWorkoutCount());

        assertThrows(WorkoutNotFoundException.class, () -> service.removeWorkout(999));
    }

    @Test
    @DisplayName("filterByType should return matching categories only")
    void testFilterByType() {
        List<WorkoutSession> cardioOnly = service.filterByType("Cardio");
        assertEquals(1, cardioOnly.size());
        assertEquals("Cardio", cardioOnly.get(0).getWorkoutType());

        List<WorkoutSession> strengthOnly = service.filterByType("Strength");
        assertEquals(1, strengthOnly.size());

        List<WorkoutSession> unknown = service.filterByType("NonExistent");
        assertTrue(unknown.isEmpty());
    }

    @Test
    @DisplayName("sortByNaturalDate sorts newest to oldest")
    void testSortByNaturalDate() {
        List<WorkoutSession> sorted = service.sortByNaturalDate();
        assertEquals(LocalDate.of(2026, 11, 20), sorted.get(0).getDate());
        assertEquals(LocalDate.of(2026, 11, 15), sorted.get(1).getDate());
        assertEquals(LocalDate.of(2026, 11, 10), sorted.get(2).getDate());
    }

    @Test
    @DisplayName("sortByDurationAscending sorts shortest to longest duration")
    void testSortByDurationAscending() {
        List<WorkoutSession> sorted = service.sortByDurationAscending();
        assertEquals(20, sorted.get(0).getDurationMinutes());
        assertEquals(30, sorted.get(1).getDurationMinutes());
        assertEquals(60, sorted.get(2).getDurationMinutes());
    }

    @Test
    @DisplayName("sortByDurationDescending sorts longest to shortest duration")
    void testSortByDurationDescending() {
        List<WorkoutSession> sorted = service.sortByDurationDescending();
        assertEquals(60, sorted.get(0).getDurationMinutes());
        assertEquals(30, sorted.get(1).getDurationMinutes());
        assertEquals(20, sorted.get(2).getDurationMinutes());
    }

    @Test
    @DisplayName("sortByName sorts alphabetically by title")
    void testSortByName() {
        List<WorkoutSession> sorted = service.sortByName();
        assertEquals("Bench Press", sorted.get(0).getTitle());
        assertEquals("Morning Run", sorted.get(1).getTitle());
        assertEquals("Stretching", sorted.get(2).getTitle());
    }

    @Test
    @DisplayName("Aggregate statistics calculation should be accurate")
    void testStatistics() {
        assertEquals(110, service.getTotalDurationMinutes());
        assertEquals(110.0 / 3.0, service.getAverageDurationMinutes(), 0.001);
        assertTrue(service.getTotalCaloriesBurned() > 0);
    }
}
