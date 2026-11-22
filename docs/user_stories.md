# FitTrack - User Stories & Requirements

## Problem Statement
Fitness enthusiasts and athletes frequently engage in diverse workout modalities (cardio, strength training, and flexibility/yoga). Tracking energy expenditure across these modalities requires distinct calculations:
- Cardio activities benefit from heart-rate telemetry or pace metrics.
- Resistance and flexibility workouts require metabolic equivalent (MET) intensity modeling.
Athletes need a lightweight, offline-first desktop tool to log diverse sessions, compare energy expenditure using interchangeable algorithmic strategies, sort sessions by metrics, and preserve data persistently.

---

## User Stories

### Epic 1: Workout Logging & Domain Modeling
- **US1.1:** As an athlete, I want to log a **Cardio workout** by entering distance, duration, and optional average heart rate, so that my pace and aerobic exertion are recorded.
- **US1.2:** As an athlete, I want to log a **Strength workout** by entering sets, repetitions, and working weight, so that total lifted tonnage is calculated.
- **US1.3:** As an athlete, I want to log a **Flexibility & Yoga workout** by specifying a focus area and intensity rating (1-5), so that my mobility routine is tracked.
- **US1.4:** As a user, I want the system to reject invalid data (e.g. negative duration, unrealistic weight or heart rate) with clear error messages (`InvalidWorkoutDataException`), preventing corrupted entries.

### Epic 2: Algorithmic Strategy (Design Pattern)
- **US2.1:** As a user, I want the system to calculate burned calories using a **MET-based Strategy** (intensity index $\times$ body weight $\times$ time) when no heart-rate monitor was worn.
- **US2.2:** As a user, I want the system to switch to a **Heart-Rate Strategy** (Keytel formula accounting for HR, age, weight, and biological gender) for higher precision whenever heart rate telemetry exists.
- **US2.3:** As an athlete, I want to switch the active calculation strategy at runtime to compare energy estimations across models without modifying logged sessions.

### Epic 3: Data Organization, Sorting & Filtering
- **US3.1:** As a user, I want to list all workouts sorted by default chronologically (newest first) using `Comparable<WorkoutSession>`.
- **US3.2:** As a user, I want to sort workouts by duration (shortest/longest) and title alphabetically using `Comparator`.
- **US3.3:** As a user, I want to sort workouts by estimated calories burned using dynamic Lambda expressions.
- **US3.4:** As a user, I want to filter workouts by category (Cardio, Strength, Flexibility).

### Epic 4: Persistence & Data Safety
- **US4.1:** As a user, I want my workout history and profile to be saved to and restored from a JSON file using Google Gson, so my data persists between application runs.
- **US4.2:** As a user, I want all file operations to use safe resource management (`try-with-resources`) to prevent file locks or data loss.
