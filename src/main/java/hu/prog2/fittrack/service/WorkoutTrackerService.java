package hu.prog2.fittrack.service;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;
import hu.prog2.fittrack.exception.WorkoutNotFoundException;
import hu.prog2.fittrack.model.UserProfile;
import hu.prog2.fittrack.model.WorkoutSession;
import hu.prog2.fittrack.persistence.WorkoutData;
import hu.prog2.fittrack.persistence.WorkoutRepository;
import hu.prog2.fittrack.strategy.CalorieCalculationStrategy;
import hu.prog2.fittrack.strategy.HeartRateStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Service managing domain workouts, sorting policies, statistics, and persistence.
 * Demonstrates high cohesion and low coupling by depending on the WorkoutRepository abstraction.
 */
public class WorkoutTrackerService {
    private final WorkoutRepository repository;
    private final List<WorkoutSession> sessions = new ArrayList<>();
    private UserProfile userProfile;
    private CalorieCalculationStrategy activeStrategy;

    public WorkoutTrackerService(WorkoutRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("WorkoutRepository cannot be null.");
        }
        this.repository = repository;
        this.userProfile = UserProfile.createDefault();
        this.activeStrategy = new HeartRateStrategy();
    }

    public synchronized void addWorkout(WorkoutSession session) throws InvalidWorkoutDataException {
        if (session == null) {
            throw new InvalidWorkoutDataException("Cannot add null workout session.");
        }
        for (WorkoutSession existing : sessions) {
            if (existing.getId() == session.getId()) {
                throw new InvalidWorkoutDataException("Workout session with ID " + session.getId() + " already exists.");
            }
        }
        sessions.add(session);
    }

    public synchronized WorkoutSession removeWorkout(int id) throws WorkoutNotFoundException {
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getId() == id) {
                return sessions.remove(i);
            }
        }
        throw new WorkoutNotFoundException(id);
    }

    public synchronized WorkoutSession findWorkoutById(int id) throws WorkoutNotFoundException {
        for (WorkoutSession session : sessions) {
            if (session.getId() == id) {
                return session;
            }
        }
        throw new WorkoutNotFoundException(id);
    }

    public synchronized List<WorkoutSession> getAllWorkouts() {
        return Collections.unmodifiableList(new ArrayList<>(sessions));
    }

    public synchronized List<WorkoutSession> filterByType(String workoutType) {
        if (workoutType == null || workoutType.isBlank()) {
            return new ArrayList<>(sessions);
        }
        List<WorkoutSession> filtered = new ArrayList<>();
        for (WorkoutSession session : sessions) {
            if (session.getWorkoutType().equalsIgnoreCase(workoutType.trim())) {
                filtered.add(session);
            }
        }
        return filtered;
    }

    /**
     * Natural ordering sorting via Comparable: newest workouts first.
     */
    public synchronized List<WorkoutSession> sortByNaturalDate() {
        List<WorkoutSession> copy = new ArrayList<>(sessions);
        copy.sort(null); // Triggers Comparable.compareTo
        return copy;
    }

    /**
     * Sorts sessions by duration ascending using method reference Comparator.
     */
    public synchronized List<WorkoutSession> sortByDurationAscending() {
        List<WorkoutSession> copy = new ArrayList<>(sessions);
        copy.sort(Comparator.comparingInt(WorkoutSession::getDurationMinutes));
        return copy;
    }

    /**
     * Sorts sessions by duration descending using reversed Comparator.
     */
    public synchronized List<WorkoutSession> sortByDurationDescending() {
        List<WorkoutSession> copy = new ArrayList<>(sessions);
        copy.sort(Comparator.comparingInt(WorkoutSession::getDurationMinutes).reversed());
        return copy;
    }

    /**
     * Sorts sessions by estimated calories burned descending using a custom Lambda expression.
     */
    public synchronized List<WorkoutSession> sortByCaloriesDescending() {
        List<WorkoutSession> copy = new ArrayList<>(sessions);
        copy.sort((w1, w2) -> {
            double c1 = w1.calculateCalories(userProfile, activeStrategy);
            double c2 = w2.calculateCalories(userProfile, activeStrategy);
            return Double.compare(c2, c1);
        });
        return copy;
    }

    /**
     * Sorts sessions alphabetically by title.
     */
    public synchronized List<WorkoutSession> sortByName() {
        List<WorkoutSession> copy = new ArrayList<>(sessions);
        copy.sort(Comparator.comparing(WorkoutSession::getTitle, String.CASE_INSENSITIVE_ORDER));
        return copy;
    }

    public synchronized int getTotalDurationMinutes() {
        int total = 0;
        for (WorkoutSession session : sessions) {
            total += session.getDurationMinutes();
        }
        return total;
    }

    public synchronized double getTotalCaloriesBurned() {
        double total = 0.0;
        for (WorkoutSession session : sessions) {
            total += session.calculateCalories(userProfile, activeStrategy);
        }
        return Math.round(total * 10.0) / 10.0;
    }

    public synchronized double getAverageDurationMinutes() {
        if (sessions.isEmpty()) {
            return 0.0;
        }
        return (double) getTotalDurationMinutes() / sessions.size();
    }

    public synchronized int getWorkoutCount() {
        return sessions.size();
    }

    public synchronized void saveData(String filePath) throws IOException {
        WorkoutData data = new WorkoutData(userProfile, sessions);
        repository.save(data, filePath);
    }

    public synchronized void loadData(String filePath) throws IOException {
        WorkoutData data = repository.load(filePath);
        if (data.getProfile() != null) {
            this.userProfile = data.getProfile();
        }
        this.sessions.clear();
        if (data.getSessions() != null) {
            this.sessions.addAll(data.getSessions());
        }
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("UserProfile cannot be null.");
        }
        this.userProfile = userProfile;
    }

    public CalorieCalculationStrategy getActiveStrategy() {
        return activeStrategy;
    }

    public void setActiveStrategy(CalorieCalculationStrategy activeStrategy) {
        if (activeStrategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null.");
        }
        this.activeStrategy = activeStrategy;
    }

    public WorkoutRepository getRepository() {
        return repository;
    }
}
