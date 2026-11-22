package hu.prog2.fittrack.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.prog2.fittrack.model.WorkoutSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * Concrete repository implementation storing application state in JSON format via Google Gson.
 * Conforms to modern Java I/O practices utilizing try-with-resources.
 */
public class GsonWorkoutRepository implements WorkoutRepository {
    private final Gson gson;

    public GsonWorkoutRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(WorkoutSession.class, new WorkoutSessionAdapter())
                .create();
    }

    @Override
    public void save(WorkoutData data, String destination) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("Workout data to persist cannot be null.");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination file path cannot be null or blank.");
        }

        Path path = Paths.get(destination);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        }
    }

    @Override
    public WorkoutData load(String source) throws IOException {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Source file path cannot be null or blank.");
        }

        Path path = Paths.get(source);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Data file not found at: " + source);
        }

        WorkoutData data;
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            data = gson.fromJson(reader, WorkoutData.class);
        }

        if (data == null) {
            data = new WorkoutData();
        }

        // Synchronize static ID counter with existing sessions
        int maxId = 0;
        if (data.getSessions() != null) {
            for (WorkoutSession session : data.getSessions()) {
                if (session != null && session.getId() > maxId) {
                    maxId = session.getId();
                }
            }
        }
        WorkoutSession.resetNextId(maxId + 1);

        return data;
    }

    public Gson getGson() {
        return gson;
    }
}
