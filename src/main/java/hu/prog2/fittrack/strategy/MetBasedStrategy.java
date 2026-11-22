package hu.prog2.fittrack.strategy;

import hu.prog2.fittrack.model.UserProfile;
import hu.prog2.fittrack.model.WorkoutSession;

/**
 * Concrete Strategy: Metabolic Equivalent of Task (MET) formula.
 * Standard calculation used when heart rate tracking is not available.
 * Formula: Calories = MET * Weight (kg) * Duration (hours)
 */
public class MetBasedStrategy implements CalorieCalculationStrategy {

    @Override
    public double calculateCalories(WorkoutSession session, UserProfile profile) {
        if (session == null || profile == null) {
            return 0.0;
        }

        double met = session.getBaseMetValue();
        double weightKg = profile.getWeightKg();
        double durationHours = session.getDurationMinutes() / 60.0;

        double calories = met * weightKg * durationHours;
        return Math.max(0.0, Math.round(calories * 10.0) / 10.0);
    }

    @Override
    public String getStrategyName() {
        return "MET-Based (Metabolic Equivalent)";
    }

    @Override
    public String getStrategyDescription() {
        return "Calculates expenditure based on exercise MET intensity, body weight, and duration.";
    }
}
