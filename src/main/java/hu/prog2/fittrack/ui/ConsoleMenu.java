package hu.prog2.fittrack.ui;

import hu.prog2.fittrack.exception.InvalidWorkoutDataException;
import hu.prog2.fittrack.exception.WorkoutNotFoundException;
import hu.prog2.fittrack.model.*;
import hu.prog2.fittrack.service.WorkoutTrackerService;
import hu.prog2.fittrack.strategy.CalorieCalculationStrategy;
import hu.prog2.fittrack.strategy.HeartRateStrategy;
import hu.prog2.fittrack.strategy.MetBasedStrategy;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console User Interface (CLI) for FitTrack.
 * Handles user input, menu navigation, and presents clear error feedback
 * on business exception conditions.
 */
public class ConsoleMenu {
    private static final String DEFAULT_SAVE_PATH = "data/workouts.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final WorkoutTrackerService service;
    private final Scanner scanner;
    private boolean running = true;

    public ConsoleMenu(WorkoutTrackerService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printWelcomeBanner();
        while (running) {
            printMainMenu();
            int choice = readInt("Select an option (0-9): ", 0, 9);
            System.out.println();
            handleChoice(choice);
            System.out.println();
        }
        System.out.println("Thank you for using FitTrack. Stay healthy and active!");
    }

    private void printWelcomeBanner() {
        System.out.println("===============================================================");
        System.out.println("               FITTRACK - WORKOUT & CALORIE TRACKER           ");
        System.out.println("        Java OOP Projectwork | Strategy Pattern & Persistence ");
        System.out.println("===============================================================");
        System.out.printf("Active Profile: %s%n", service.getUserProfile().getName());
        System.out.printf("Active Strategy: %s%n", service.getActiveStrategy().getStrategyName());
        System.out.println("===============================================================");
    }

    private void printMainMenu() {
        System.out.println("--- MAIN MENU ---");
        System.out.println("1. Log New Workout");
        System.out.println("2. View & Sort Workouts");
        System.out.println("3. Filter Workouts by Category");
        System.out.println("4. Inspect Workout Details by ID");
        System.out.println("5. Delete Workout by ID");
        System.out.println("6. View Statistics & Calorie Summary");
        System.out.println("7. Switch Calorie Calculation Strategy");
        System.out.println("8. View / Edit User Profile");
        System.out.println("9. Save / Reload Data (JSON)");
        System.out.println("0. Exit Application");
    }

    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> logNewWorkout();
                case 2 -> listAndSortWorkouts();
                case 3 -> filterWorkouts();
                case 4 -> inspectWorkout();
                case 5 -> deleteWorkout();
                case 6 -> showStatistics();
                case 7 -> switchStrategy();
                case 8 -> manageProfile();
                case 9 -> managePersistence();
                case 0 -> running = false;
                default -> System.out.println("Invalid option.");
            }
        } catch (InvalidWorkoutDataException e) {
            System.out.println("[Domain Validation Error] " + e.getMessage());
        } catch (WorkoutNotFoundException e) {
            System.out.println("[Not Found Error] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[System Error] An unexpected error occurred: " + e.getMessage());
        }
    }

    private void logNewWorkout() throws InvalidWorkoutDataException {
        System.out.println("--- LOG NEW WORKOUT ---");
        System.out.println("Choose workout category:");
        System.out.println("  1. Cardio (Running, Cycling, Swimming, Rowing)");
        System.out.println("  2. Strength (Weight training, Calisthenics)");
        System.out.println("  3. Flexibility & Yoga (Stretching, Mobility, Pilates)");
        int typeChoice = readInt("Select category (1-3): ", 1, 3);

        String title = readString("Workout title/name: ");
        LocalDate date = readDate("Workout date (yyyy-MM-dd, empty for today): ");
        int duration = readInt("Duration in minutes (1-1440): ", 1, 1440);

        WorkoutSession session;
        switch (typeChoice) {
            case 1 -> {
                double distance = readDouble("Distance covered in km (0.1 - 500): ", 0.1, 500.0);
                int hr = readInt("Average heart rate bpm (0 if not recorded, 40-240): ", 0, 240);
                session = new CardioWorkout(title, date, duration, distance, hr);
            }
            case 2 -> {
                int sets = readInt("Number of sets (1-100): ", 1, 100);
                int reps = readInt("Average reps per set (1-200): ", 1, 200);
                double weight = readDouble("Working weight in kg (0 for bodyweight): ", 0.0, 1000.0);
                session = new StrengthWorkout(title, date, duration, sets, reps, weight);
            }
            case 3 -> {
                String focus = readString("Focus area (e.g., Full Body, Hamstrings, Core): ");
                int intensity = readInt("Intensity level (1 = Gentle, 5 = Intense): ", 1, 5);
                session = new FlexibilityWorkout(title, date, duration, focus, intensity);
            }
            default -> throw new IllegalStateException("Unexpected category choice.");
        }

        service.addWorkout(session);
        double estCalories = session.calculateCalories(service.getUserProfile(), service.getActiveStrategy());
        System.out.printf("[Success] Logged %s #%d: '%s' | Est. Burned: %.1f kcal (%s)%n",
                session.getWorkoutType(), session.getId(), session.getTitle(),
                estCalories, service.getActiveStrategy().getStrategyName());
    }

    private void listAndSortWorkouts() {
        if (service.getWorkoutCount() == 0) {
            System.out.println("No workout sessions logged yet.");
            return;
        }

        System.out.println("--- SORTING OPTIONS ---");
        System.out.println("1. Natural Ordering (Date: Most recent first) [Comparable]");
        System.out.println("2. Duration Ascending (Shortest first) [Comparator]");
        System.out.println("3. Duration Descending (Longest first) [Comparator reversed]");
        System.out.println("4. Estimated Calories Descending (Highest burn first) [Lambda]");
        System.out.println("5. Alphabetical by Title [Comparator]");
        int sortChoice = readInt("Select sorting method (1-5): ", 1, 5);

        List<WorkoutSession> sorted = switch (sortChoice) {
            case 1 -> service.sortByNaturalDate();
            case 2 -> service.sortByDurationAscending();
            case 3 -> service.sortByDurationDescending();
            case 4 -> service.sortByCaloriesDescending();
            case 5 -> service.sortByName();
            default -> service.getAllWorkouts();
        };

        displayWorkoutTable(sorted);
    }

    private void filterWorkouts() {
        System.out.println("--- FILTER WORKOUTS ---");
        System.out.println("Enter category filter: Cardio, Strength, or Flexibility");
        String category = readString("Category name: ");

        List<WorkoutSession> filtered = service.filterByType(category);
        if (filtered.isEmpty()) {
            System.out.println("No workouts found for category: " + category);
        } else {
            System.out.printf("Found %d matching session(s):%n", filtered.size());
            displayWorkoutTable(filtered);
        }
    }

    private void inspectWorkout() throws WorkoutNotFoundException {
        int id = readInt("Enter workout ID to inspect: ", 1, Integer.MAX_VALUE);
        WorkoutSession session = service.findWorkoutById(id);

        System.out.println("==================================================");
        System.out.println("WORKOUT DETAILS");
        System.out.println("==================================================");
        System.out.printf("ID:            #%d%n", session.getId());
        System.out.printf("Title:         %s%n", session.getTitle());
        System.out.printf("Category:      %s%n", session.getWorkoutType());
        System.out.printf("Date:          %s%n", session.getDate());
        System.out.printf("Duration:      %d minutes%n", session.getDurationMinutes());
        System.out.printf("Details:       %s%n", session.getSpecificDetails());
        System.out.printf("MET Value:     %.1f MET%n", session.getBaseMetValue());
        System.out.println("--------------------------------------------------");
        System.out.println("Calorie Estimations across Strategies:");
        MetBasedStrategy metStrategy = new MetBasedStrategy();
        HeartRateStrategy hrStrategy = new HeartRateStrategy();
        System.out.printf("  • %-30s: %.1f kcal%n", metStrategy.getStrategyName(),
                session.calculateCalories(service.getUserProfile(), metStrategy));
        System.out.printf("  • %-30s: %.1f kcal%n", hrStrategy.getStrategyName(),
                session.calculateCalories(service.getUserProfile(), hrStrategy));
        System.out.println("==================================================");
    }

    private void deleteWorkout() throws WorkoutNotFoundException {
        int id = readInt("Enter workout ID to delete: ", 1, Integer.MAX_VALUE);
        WorkoutSession removed = service.removeWorkout(id);
        System.out.printf("[Success] Removed workout #%d: '%s' (%s)%n",
                removed.getId(), removed.getTitle(), removed.getWorkoutType());
    }

    private void showStatistics() {
        System.out.println("==================================================");
        System.out.println("              WORKOUT & ENERGY SUMMARY            ");
        System.out.println("==================================================");
        System.out.printf("Total workouts logged:      %d%n", service.getWorkoutCount());
        System.out.printf("Total active time:          %d minutes (%.1f hours)%n",
                service.getTotalDurationMinutes(), service.getTotalDurationMinutes() / 60.0);
        System.out.printf("Average workout duration:   %.1f minutes%n", service.getAverageDurationMinutes());
        System.out.printf("Total estimated calories:   %.1f kcal%n", service.getTotalCaloriesBurned());
        System.out.printf("Active strategy:            %s%n", service.getActiveStrategy().getStrategyName());
        System.out.println("==================================================");
    }

    private void switchStrategy() {
        System.out.println("--- SWITCH CALORIE CALCULATION STRATEGY (Strategy Pattern) ---");
        System.out.printf("Current Strategy: %s%n", service.getActiveStrategy().getStrategyName());
        System.out.println("Available strategies:");
        System.out.println("  1. Heart-Rate Based (Keytel Precision) - Accounts for HR, age, weight, gender");
        System.out.println("  2. MET-Based (Metabolic Equivalent)   - Accounts for MET intensity and weight");
        int choice = readInt("Select strategy (1-2): ", 1, 2);

        CalorieCalculationStrategy newStrategy = (choice == 1) ? new HeartRateStrategy() : new MetBasedStrategy();
        service.setActiveStrategy(newStrategy);
        System.out.printf("[Success] Switched active strategy to: %s%n", newStrategy.getStrategyName());
        System.out.printf("Description: %s%n", newStrategy.getStrategyDescription());
    }

    private void manageProfile() throws InvalidWorkoutDataException {
        UserProfile p = service.getUserProfile();
        System.out.println("--- USER PROFILE ---");
        System.out.println(p);
        System.out.println("Would you like to edit your profile details? (y/n)");
        String ans = readString("> ");
        if (!ans.equalsIgnoreCase("y") && !ans.equalsIgnoreCase("yes")) {
            return;
        }

        String name = readString("New name [" + p.getName() + "]: ");
        if (name.isBlank()) name = p.getName();

        int age = readInt("New age [" + p.getAge() + "] (10-120): ", 10, 120);
        double weight = readDouble("New weight in kg [" + p.getWeightKg() + "] (30-350): ", 30.0, 350.0);

        System.out.println("Select Gender: 1. Male, 2. Female, 3. Other");
        int gChoice = readInt("Choice (1-3): ", 1, 3);
        Gender gender = switch (gChoice) {
            case 1 -> Gender.MALE;
            case 2 -> Gender.FEMALE;
            default -> Gender.OTHER;
        };

        int restingHr = readInt("Resting heart rate bpm [" + p.getRestingHeartRate() + "] (35-150): ", 35, 150);

        UserProfile updated = new UserProfile(name, age, weight, gender, restingHr);
        service.setUserProfile(updated);
        System.out.println("[Success] Profile updated successfully!");
    }

    private void managePersistence() {
        System.out.println("--- PERSISTENCE (JSON with Google Gson) ---");
        System.out.println("1. Save all data to JSON file");
        System.out.println("2. Reload data from JSON file");
        int choice = readInt("Choice (1-2): ", 1, 2);

        String path = readString("File path [press Enter for default '" + DEFAULT_SAVE_PATH + "']: ");
        if (path.isBlank()) {
            path = DEFAULT_SAVE_PATH;
        }

        try {
            if (choice == 1) {
                service.saveData(path);
                System.out.println("[Success] Data successfully saved to: " + path);
            } else {
                service.loadData(path);
                System.out.printf("[Success] Data successfully loaded from: %s (%d sessions)%n",
                        path, service.getWorkoutCount());
            }
        } catch (IOException e) {
            System.out.println("[I/O Error] Could not complete persistence operation: " + e.getMessage());
        }
    }

    private void displayWorkoutTable(List<WorkoutSession> list) {
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-12s | %-20s | %-10s | %-8s | %-8s | %s%n",
                "ID", "Category", "Title", "Date", "Duration", "Calories", "Details");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        for (WorkoutSession s : list) {
            double cal = s.calculateCalories(service.getUserProfile(), service.getActiveStrategy());
            System.out.printf("#%-4d | %-12s | %-20s | %-10s | %4d min | %6.1f k | %s%n",
                    s.getId(), s.getWorkoutType(), truncate(s.getTitle(), 20),
                    s.getDate(), s.getDurationMinutes(), cal, s.getSpecificDetails());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input. Please enter an integer.");
            }
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().replace(',', '.');
            try {
                double val = Double.parseDouble(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter a number between %.1f and %.1f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid decimal input. Please enter a valid number.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(line, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use 'yyyy-MM-dd' (e.g., 2026-11-20).");
            }
        }
    }
}
