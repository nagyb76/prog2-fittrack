package hu.prog2.fittrack.model;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;

import java.time.LocalDate;

/**
 * Represents an endurance / cardiovascular workout (running, cycling, swimming, rowing).
 */
public class CardioWorkout extends WorkoutSession {
    private double distanceKm;
    private int averageHeartRate; // 0 if not measured

    public CardioWorkout(String title, LocalDate date, int durationMinutes, double distanceKm, int averageHeartRate)
            throws InvalidWorkoutDataException {
        super(title, date, durationMinutes);
        validateCardio(distanceKm, averageHeartRate);
        this.distanceKm = distanceKm;
        this.averageHeartRate = averageHeartRate;
    }

    public CardioWorkout(int id, String title, LocalDate date, int durationMinutes, double distanceKm, int averageHeartRate)
            throws InvalidWorkoutDataException {
        super(id, title, date, durationMinutes);
        validateCardio(distanceKm, averageHeartRate);
        this.distanceKm = distanceKm;
        this.averageHeartRate = averageHeartRate;
    }

    private static void validateCardio(double distanceKm, int averageHeartRate) throws InvalidWorkoutDataException {
        if (!Double.isFinite(distanceKm) || distanceKm <= 0.0 || distanceKm > 500.0) {
            throw new InvalidWorkoutDataException("Cardio distance must be positive and realistic (up to 500 km).");
        }
        if (averageHeartRate < 0 || averageHeartRate > 240) {
            throw new InvalidWorkoutDataException("Average heart rate must be between 0 (not recorded) and 240 bpm.");
        }
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) throws InvalidWorkoutDataException {
        validateCardio(distanceKm, this.averageHeartRate);
        this.distanceKm = distanceKm;
    }

    public int getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(int averageHeartRate) throws InvalidWorkoutDataException {
        validateCardio(this.distanceKm, averageHeartRate);
        this.averageHeartRate = averageHeartRate;
    }

    public double getAverageSpeedKmH() {
        return distanceKm / (getDurationMinutes() / 60.0);
    }

    @Override
    public String getWorkoutType() {
        return "Cardio";
    }

    @Override
    public String getSpecificDetails() {
        String hrStr = averageHeartRate > 0 ? averageHeartRate + " bpm" : "N/A";
        return String.format(java.util.Locale.US, "Distance: %.2f km, Avg Speed: %.1f km/h, Avg HR: %s",
                distanceKm, getAverageSpeedKmH(), hrStr);
    }

    @Override
    public double getBaseMetValue() {
        double speed = getAverageSpeedKmH();
        if (speed >= 12.0) return 11.5; // Fast running
        if (speed >= 9.5) return 9.8;   // Moderate running
        if (speed >= 7.0) return 7.0;   // Jogging
        return 5.5;                    // Light cardio / cycling / brisk walking
    }
}
