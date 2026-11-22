package hu.prog2.fittrack.persistence;

import java.io.IOException;

/**
 * Storage contract for persisting and retrieving fitness application data.
 * Abstracts the physical persistence mechanism (JSON file, database, etc.) from the service layer.
 */
public interface WorkoutRepository {

    /**
     * Saves application data to the specified destination.
     *
     * @param data        the workout data to persist
     * @param destination the target file path
     * @throws IOException if an I/O error occurs
     */
    void save(WorkoutData data, String destination) throws IOException;

    /**
     * Loads application data from the specified source.
     *
     * @param source the source file path
     * @return the recovered WorkoutData instance
     * @throws IOException if an I/O error occurs or file is not found
     */
    WorkoutData load(String source) throws IOException;
}
