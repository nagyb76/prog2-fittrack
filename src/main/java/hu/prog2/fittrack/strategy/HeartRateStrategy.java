package hu.prog2.fittrack.strategy;

import hu.prog2.fittrack.model.CardioWorkout;
import hu.prog2.fittrack.model.Gender;
import hu.prog2.fittrack.model.UserProfile;
import hu.prog2.fittrack.model.WorkoutSession;

/**
 * Concrete Strategy: Heart Rate Energy Expenditure calculation (Keytel formula).
 * Uses biometric heart rate data, gender, age, and weight for higher precision.
 * Falls back to MET-based calculation if heart rate was not recorded for the session.
 */
public class HeartRateStrategy implements CalorieCalculationStrategy {
    private final MetBasedStrategy fallbackStrategy = new MetBasedStrategy();

    @Override
    public double calculateCalories(WorkoutSession session, UserProfile profile) {
        if (session == null || profile == null) {
            return 0.0;
        }

        // Check if session contains recorded heart rate
        int heartRate = 0;
        if (session instanceof CardioWorkout) {
            heartRate = ((CardioWorkout) session).getAverageHeartRate();
        }

        // If heart rate was not recorded, delegate to MET-based calculation
        if (heartRate <= 0) {
            return fallbackStrategy.calculateCalories(session, profile);
        }

        int age = profile.getAge();
        double weight = profile.getWeightKg();
        double durationMinutes = session.getDurationMinutes();

        double caloriesPerMinute;
        if (profile.getGender() == Gender.FEMALE) {
            // Keytel formula for women (converting kJ to kcal via / 4.184)
            caloriesPerMinute = ((-20.4022 + (0.4472 * heartRate) - (0.1263 * weight) + (0.074 * age)) / 4.184);
        } else {
            // Keytel formula for men / other
            caloriesPerMinute = ((-55.0969 + (0.6309 * heartRate) + (0.1988 * weight) + (0.2017 * age)) / 4.184);
        }

        double totalCalories = Math.max(0.0, caloriesPerMinute * durationMinutes);
        return Math.round(totalCalories * 10.0) / 10.0;
    }

    @Override
    public String getStrategyName() {
        return "Heart-Rate Based (Keytel Precision)";
    }

    @Override
    public String getStrategyDescription() {
        return "Calculates expenditure from real heart rate, age, weight, and gender; falls back to MET if no HR data.";
    }
}
