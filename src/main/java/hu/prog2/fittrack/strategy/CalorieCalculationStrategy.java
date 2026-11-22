package hu.prog2.fittrack.strategy;

import hu.prog2.fittrack.model.UserProfile;
import hu.prog2.fittrack.model.WorkoutSession;

/**
 * Strategy interface representing interchangeable calorie estimation algorithms.
 * Encapsulates the varying calorie computation logic away from the domain model,
 * satisfying the Open/Closed Principle and the Strategy design pattern.
 */
public interface CalorieCalculationStrategy {

    /**
     * Calculates estimated calories burned during a specific workout session for a user profile.
     *
     * @param session the workout session
     * @param profile the user's physical profile
     * @return estimated calories burned (kcal)
     */
    double calculateCalories(WorkoutSession session, UserProfile profile);

    /**
     * Human-readable name of the strategy.
     */
    String getStrategyName();

    /**
     * Short explanation of how this strategy calculates calories.
     */
    String getStrategyDescription();
}
