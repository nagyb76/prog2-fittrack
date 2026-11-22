package hu.prog2.fittrack.exception;

/**
 * Custom checked exception thrown when a workout session with a requested identifier
 * cannot be found in the system.
 */
public class WorkoutNotFoundException extends Exception {
    private final int workoutId;

    public WorkoutNotFoundException(int workoutId) {
        super("Workout session not found with ID: " + workoutId);
        this.workoutId = workoutId;
    }

    public int getWorkoutId() {
        return workoutId;
    }
}
