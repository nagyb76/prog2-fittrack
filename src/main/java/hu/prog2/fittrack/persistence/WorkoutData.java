package hu.prog2.fittrack.persistence;

import hu.prog2.fittrack.model.UserProfile;
import hu.prog2.fittrack.model.WorkoutSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object encapsulating all persistent application state:
 * the user profile and the collection of workout sessions.
 */
public class WorkoutData {
    private UserProfile profile;
    private List<WorkoutSession> sessions;

    public WorkoutData() {
        this.profile = UserProfile.createDefault();
        this.sessions = new ArrayList<>();
    }

    public WorkoutData(UserProfile profile, List<WorkoutSession> sessions) {
        this.profile = profile != null ? profile : UserProfile.createDefault();
        this.sessions = sessions != null ? new ArrayList<>(sessions) : new ArrayList<>();
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public List<WorkoutSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<WorkoutSession> sessions) {
        this.sessions = sessions != null ? new ArrayList<>(sessions) : new ArrayList<>();
    }
}
