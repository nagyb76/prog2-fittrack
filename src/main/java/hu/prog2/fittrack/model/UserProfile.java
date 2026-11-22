package hu.prog2.fittrack.model;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;

/**
 * Represents the athlete/user profile containing biometrical metrics
 * needed for personalized workout and calorie estimations.
 */
public class UserProfile {
    public static final double DEFAULT_WEIGHT_KG = 70.0;
    public static final int DEFAULT_AGE = 25;
    public static final int DEFAULT_RESTING_HEART_RATE = 65;

    private String name;
    private int age;
    private double weightKg;
    private Gender gender;
    private int restingHeartRate;

    public UserProfile(String name, int age, double weightKg, Gender gender, int restingHeartRate)
            throws InvalidWorkoutDataException {
        validateData(name, age, weightKg, gender, restingHeartRate);
        this.name = name.trim();
        this.age = age;
        this.weightKg = weightKg;
        this.gender = gender;
        this.restingHeartRate = restingHeartRate;
    }

    public static UserProfile createDefault() {
        try {
            return new UserProfile("Athlete", DEFAULT_AGE, DEFAULT_WEIGHT_KG, Gender.MALE, DEFAULT_RESTING_HEART_RATE);
        } catch (InvalidWorkoutDataException e) {
            throw new IllegalStateException("Default user profile parameters must always be valid", e);
        }
    }

    private static void validateData(String name, int age, double weightKg, Gender gender, int restingHeartRate)
            throws InvalidWorkoutDataException {
        if (name == null || name.isBlank()) {
            throw new InvalidWorkoutDataException("Profile name cannot be null or empty.");
        }
        if (age < 10 || age > 120) {
            throw new InvalidWorkoutDataException("Age must be between 10 and 120 years (received: " + age + ").");
        }
        if (!Double.isFinite(weightKg) || weightKg < 30.0 || weightKg > 350.0) {
            throw new InvalidWorkoutDataException("Weight must be between 30 kg and 350 kg (received: " + weightKg + ").");
        }
        if (gender == null) {
            throw new InvalidWorkoutDataException("Gender cannot be null.");
        }
        if (restingHeartRate < 35 || restingHeartRate > 150) {
            throw new InvalidWorkoutDataException("Resting heart rate must be between 35 and 150 bpm (received: " + restingHeartRate + ").");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidWorkoutDataException {
        if (name == null || name.isBlank()) {
            throw new InvalidWorkoutDataException("Profile name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) throws InvalidWorkoutDataException {
        if (age < 10 || age > 120) {
            throw new InvalidWorkoutDataException("Age must be between 10 and 120 years.");
        }
        this.age = age;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) throws InvalidWorkoutDataException {
        if (!Double.isFinite(weightKg) || weightKg < 30.0 || weightKg > 350.0) {
            throw new InvalidWorkoutDataException("Weight must be between 30 kg and 350 kg.");
        }
        this.weightKg = weightKg;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) throws InvalidWorkoutDataException {
        if (gender == null) {
            throw new InvalidWorkoutDataException("Gender cannot be null.");
        }
        this.gender = gender;
    }

    public int getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(int restingHeartRate) throws InvalidWorkoutDataException {
        if (restingHeartRate < 35 || restingHeartRate > 150) {
            throw new InvalidWorkoutDataException("Resting heart rate must be between 35 and 150 bpm.");
        }
        this.restingHeartRate = restingHeartRate;
    }

    @Override
    public String toString() {
        return String.format("%s (Age: %d, Weight: %.1f kg, Gender: %s, Resting HR: %d bpm)",
                name, age, weightKg, gender.getDisplayName(), restingHeartRate);
    }
}
