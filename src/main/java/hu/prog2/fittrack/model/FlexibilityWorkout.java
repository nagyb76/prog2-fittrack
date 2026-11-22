package hu.prog2.fittrack.model;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;

import java.time.LocalDate;

/**
 * Represents a mobility, stretching, pilates, or yoga session.
 */
public class FlexibilityWorkout extends WorkoutSession {
    private String focusArea;
    private int intensityLevel; // 1 (Gentle) to 5 (Advanced/Intense)

    public FlexibilityWorkout(String title, LocalDate date, int durationMinutes, String focusArea, int intensityLevel)
            throws InvalidWorkoutDataException {
        super(title, date, durationMinutes);
        validateFlexibility(focusArea, intensityLevel);
        this.focusArea = focusArea.trim();
        this.intensityLevel = intensityLevel;
    }

    public FlexibilityWorkout(int id, String title, LocalDate date, int durationMinutes, String focusArea, int intensityLevel)
            throws InvalidWorkoutDataException {
        super(id, title, date, durationMinutes);
        validateFlexibility(focusArea, intensityLevel);
        this.focusArea = focusArea.trim();
        this.intensityLevel = intensityLevel;
    }

    private static void validateFlexibility(String focusArea, int intensityLevel) throws InvalidWorkoutDataException {
        if (focusArea == null || focusArea.isBlank()) {
            throw new InvalidWorkoutDataException("Focus area cannot be null or blank.");
        }
        if (intensityLevel < 1 || intensityLevel > 5) {
            throw new InvalidWorkoutDataException("Intensity level must be between 1 and 5.");
        }
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) throws InvalidWorkoutDataException {
        validateFlexibility(focusArea, this.intensityLevel);
        this.focusArea = focusArea.trim();
    }

    public int getIntensityLevel() {
        return intensityLevel;
    }

    public void setIntensityLevel(int intensityLevel) throws InvalidWorkoutDataException {
        validateFlexibility(this.focusArea, intensityLevel);
        this.intensityLevel = intensityLevel;
    }

    @Override
    public String getWorkoutType() {
        return "Flexibility & Yoga";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Focus: %s, Intensity: %d/5", focusArea, intensityLevel);
    }

    @Override
    public double getBaseMetValue() {
        // Dynamic yoga / power stretching vs gentle mobility
        return 2.5 + (intensityLevel * 0.5); // Range 3.0 - 5.0
    }
}
