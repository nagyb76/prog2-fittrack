package hu.prog2.fittrack.persistence;

import com.google.gson.*;
import hu.prog2.fittrack.model.CardioWorkout;
import hu.prog2.fittrack.model.FlexibilityWorkout;
import hu.prog2.fittrack.model.StrengthWorkout;
import hu.prog2.fittrack.model.WorkoutSession;

import java.lang.reflect.Type;

/**
 * Custom Gson Serializer & Deserializer for abstract WorkoutSession hierarchy,
 * enabling polymorphic JSON persistence.
 */
public class WorkoutSessionAdapter implements JsonSerializer<WorkoutSession>, JsonDeserializer<WorkoutSession> {
    private static final String TYPE_PROPERTY = "type";
    private static final String TYPE_CARDIO = "CARDIO";
    private static final String TYPE_STRENGTH = "STRENGTH";
    private static final String TYPE_FLEXIBILITY = "FLEXIBILITY";

    @Override
    public JsonElement serialize(WorkoutSession src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (src instanceof CardioWorkout) {
            result.addProperty(TYPE_PROPERTY, TYPE_CARDIO);
            JsonObject data = context.serialize(src, CardioWorkout.class).getAsJsonObject();
            copyProperties(data, result);
        } else if (src instanceof StrengthWorkout) {
            result.addProperty(TYPE_PROPERTY, TYPE_STRENGTH);
            JsonObject data = context.serialize(src, StrengthWorkout.class).getAsJsonObject();
            copyProperties(data, result);
        } else if (src instanceof FlexibilityWorkout) {
            result.addProperty(TYPE_PROPERTY, TYPE_FLEXIBILITY);
            JsonObject data = context.serialize(src, FlexibilityWorkout.class).getAsJsonObject();
            copyProperties(data, result);
        } else {
            throw new JsonParseException("Unknown WorkoutSession subtype: " + src.getClass().getName());
        }
        return result;
    }

    private void copyProperties(JsonObject source, JsonObject target) {
        for (String key : source.keySet()) {
            if (!target.has(key)) {
                target.add(key, source.get(key));
            }
        }
    }

    @Override
    public WorkoutSession deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement typeElement = jsonObject.get(TYPE_PROPERTY);

        if (typeElement == null) {
            throw new JsonParseException("Missing polymorphic type discriminator property: " + TYPE_PROPERTY);
        }

        String typeStr = typeElement.getAsString();
        switch (typeStr) {
            case TYPE_CARDIO:
                return context.deserialize(jsonObject, CardioWorkout.class);
            case TYPE_STRENGTH:
                return context.deserialize(jsonObject, StrengthWorkout.class);
            case TYPE_FLEXIBILITY:
                return context.deserialize(jsonObject, FlexibilityWorkout.class);
            default:
                throw new JsonParseException("Unrecognized workout session type: " + typeStr);
        }
    }
}
