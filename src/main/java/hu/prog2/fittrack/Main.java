package hu.prog2.fittrack;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;
import hu.prog2.fittrack.model.*;
import hu.prog2.fittrack.persistence.GsonWorkoutRepository;
import hu.prog2.fittrack.persistence.WorkoutRepository;
import hu.prog2.fittrack.service.WorkoutTrackerService;
import hu.prog2.fittrack.ui.ConsoleMenu;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Application entry point for FitTrack.
 * Initializes the repository, seeds initial sample workouts if no persistent
 * state exists, and starts the console user interface.
 */
public class Main {
    public static final String DEFAULT_DATA_PATH = "data/workouts.json";

    public static void main(String[] args) {
        WorkoutRepository repository = new GsonWorkoutRepository();
        WorkoutTrackerService service = new WorkoutTrackerService(repository);

        File dataFile = new File(DEFAULT_DATA_PATH);
        if (dataFile.exists()) {
            try {
                service.loadData(DEFAULT_DATA_PATH);
                System.out.println("[Init] Loaded persistent session data from " + DEFAULT_DATA_PATH);
            } catch (IOException e) {
                System.out.println("[Init Warning] Could not load data file: " + e.getMessage());
                seedSampleData(service);
            }
        } else {
            seedSampleData(service);
            try {
                service.saveData(DEFAULT_DATA_PATH);
                System.out.println("[Init] Initialized and saved default sample workouts to " + DEFAULT_DATA_PATH);
            } catch (IOException e) {
                System.out.println("[Init Warning] Could not create initial save file: " + e.getMessage());
            }
        }

        ConsoleMenu menu = new ConsoleMenu(service);
        menu.start();
    }

    private static void seedSampleData(WorkoutTrackerService service) {
        try {
            UserProfile profile = new UserProfile("Athlete", 22, 75.0, Gender.MALE, 62);
            service.setUserProfile(profile);

            LocalDate today = LocalDate.now();

            service.addWorkout(new CardioWorkout(
                    "Morning Trail Run",
                    today.minusDays(3),
                    45,
                    7.5,
                    152
            ));

            service.addWorkout(new StrengthWorkout(
                    "Upper Body Hypertrophy",
                    today.minusDays(2),
                    60,
                    16,
                    10,
                    65.0
            ));

            service.addWorkout(new FlexibilityWorkout(
                    "Post-run Mobility & Yoga",
                    today.minusDays(1),
                    30,
                    "Hips & Lower Back",
                    3
            ));

            service.addWorkout(new CardioWorkout(
                    "Interval Sprint Session",
                    today,
                    35,
                    5.2,
                    168
            ));

        } catch (InvalidWorkoutDataException e) {
            System.err.println("[Init Error] Failed to seed initial data: " + e.getMessage());
        }
    }
}
