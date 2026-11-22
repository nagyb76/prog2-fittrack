package hu.prog2.fittrack.model;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract base class representing a generic workout session.
 * Demonstrates abstraction, encapsulation, and provides the contract for polymorphism.
 */
public abstract class WorkoutSession implements Comparable<WorkoutSession> {
    private static int nextId = 1;

    private final int id;
    private String title;
    private LocalDate date;
    private int durationMinutes;

    /**
     * Constructor generating a new sequential ID.
     */
    protected WorkoutSession(String title, LocalDate date, int durationMinutes)
            throws InvalidWorkoutDataException {
        this(nextId++, title, date, durationMinutes);
    }

    /**
     * Constructor accepting an explicit ID (e.g. during repository deserialization).
     */
    protected WorkoutSession(int id, String title, LocalDate date, int durationMinutes)
            throws InvalidWorkoutDataException {
        validateCommon(id, title, date, durationMinutes);
        this.id = id;
        this.title = title.trim();
        this.date = date;
        this.durationMinutes = durationMinutes;

        // Keep ID generator in sync
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    private static void validateCommon(int id, String title, LocalDate date, int durationMinutes)
            throws InvalidWorkoutDataException {
        if (id <= 0) {
            throw new InvalidWorkoutDataException("Workout ID must be a positive integer.");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidWorkoutDataException("Workout title cannot be null or blank.");
        }
        if (date == null) {
            throw new InvalidWorkoutDataException("Workout date cannot be null.");
        }
        if (durationMinutes <= 0 || durationMinutes > 1440) {
            throw new InvalidWorkoutDataException("Duration must be between 1 and 1440 minutes (24h).");
        }
    }

    public static synchronized void resetNextId(int value) {
        nextId = Math.max(1, value);
    }

    public static synchronized int getNextId() {
        return nextId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws InvalidWorkoutDataException {
        if (title == null || title.isBlank()) {
            throw new InvalidWorkoutDataException("Workout title cannot be null or blank.");
        }
        this.title = title.trim();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) throws InvalidWorkoutDataException {
        if (date == null) {
            throw new InvalidWorkoutDataException("Workout date cannot be null.");
        }
        this.date = date;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) throws InvalidWorkoutDataException {
        if (durationMinutes <= 0 || durationMinutes > 1440) {
            throw new InvalidWorkoutDataException("Duration must be between 1 and 1440 minutes.");
        }
        this.durationMinutes = durationMinutes;
    }

    /**
     * Returns the workout category name (e.g., "Cardio", "Strength", "Flexibility").
     */
    public abstract String getWorkoutType();

    /**
     * Formats category-specific parameters into a readable string.
     */
    public abstract String getSpecificDetails();

    /**
     * Returns the typical MET (Metabolic Equivalent of Task) score for this activity.
     */
    public abstract double getBaseMetValue();

    /**
     * Natural ordering: most recent workouts first, then descending by ID.
     */
    @Override
    public int compareTo(WorkoutSession other) {
        if (other == null) {
            return 1;
        }
        int dateComparison = other.date.compareTo(this.date);
        if (dateComparison != 0) {
            return dateComparison;
        }
        return Integer.compare(other.id, this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkoutSession that = (WorkoutSession) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[#%d] %s (%s) on %s | Duration: %d min | Details: %s",
                id, title, getWorkoutType(), date, durationMinutes, getSpecificDetails());
    }
}
