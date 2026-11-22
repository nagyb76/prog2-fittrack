package hu.prog2.fittrack.model;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;

import java.time.LocalDate;

/**
 * Represents a resistance or weight-training workout session.
 */
public class StrengthWorkout extends WorkoutSession {
    private int sets;
    private int reps;
    private double weightKg; // Average working weight or bodyweight addition

    public StrengthWorkout(String title, LocalDate date, int durationMinutes, int sets, int reps, double weightKg)
            throws InvalidWorkoutDataException {
        super(title, date, durationMinutes);
        validateStrength(sets, reps, weightKg);
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
    }

    public StrengthWorkout(int id, String title, LocalDate date, int durationMinutes, int sets, int reps, double weightKg)
            throws InvalidWorkoutDataException {
        super(id, title, date, durationMinutes);
        validateStrength(sets, reps, weightKg);
        this.sets = sets;
        this.reps = reps;
        this.weightKg = weightKg;
    }

    private static void validateStrength(int sets, int reps, double weightKg) throws InvalidWorkoutDataException {
        if (sets <= 0 || sets > 100) {
            throw new InvalidWorkoutDataException("Sets must be between 1 and 100.");
        }
        if (reps <= 0 || reps > 200) {
            throw new InvalidWorkoutDataException("Reps must be between 1 and 200.");
        }
        if (!Double.isFinite(weightKg) || weightKg < 0.0 || weightKg > 1000.0) {
            throw new InvalidWorkoutDataException("Weight must be non-negative and realistic (up to 1000 kg).");
        }
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) throws InvalidWorkoutDataException {
        validateStrength(sets, this.reps, this.weightKg);
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) throws InvalidWorkoutDataException {
        validateStrength(this.sets, reps, this.weightKg);
        this.reps = reps;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) throws InvalidWorkoutDataException {
        validateStrength(this.sets, this.reps, weightKg);
        this.weightKg = weightKg;
    }

    public double getTotalTonnageKg() {
        return sets * reps * weightKg;
    }

    @Override
    public String getWorkoutType() {
        return "Strength";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("%d sets x %d reps @ %.1f kg (Tonnage: %.1f kg)",
                sets, reps, weightKg, getTotalTonnageKg());
    }

    @Override
    public double getBaseMetValue() {
        // High tonnage / intensity resistance training vs light
        if (weightKg >= 80.0 || sets >= 15) {
            return 6.0; // Vigorous resistance training
        }
        return 4.0;     // Moderate resistance training
    }
}
