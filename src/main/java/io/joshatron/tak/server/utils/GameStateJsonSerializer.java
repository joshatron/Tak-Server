package io.joshatron.tak.server.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.joshatron.tak.engine.game.GameState;
import org.json.JSONObject;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class GameStateJsonSerializer extends JsonSerializer<GameState> {

    @Override
    public void serialize(GameState gameState, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        JSONObject json = gameState.exportToJson();
        jsonGenerator.writeNumberField("size", json.getInt("size"));
        jsonGenerator.writeNumberField("whiteStones", json.getInt("whiteStones"));
        jsonGenerator.writeNumberField("whiteCapstones", json.getInt("whiteCapstones"));
        jsonGenerator.writeNumberField("blackStones", json.getInt("blackStones"));
        jsonGenerator.writeNumberField("blackCapstones", json.getInt("blackCapstones"));
        jsonGenerator.writeStringField("first", json.getString("first"));
        jsonGenerator.writeStringField("current", json.getString("current"));
        jsonGenerator.writeArrayFieldStart("turns");
        for(int i = 0; i < json.getJSONArray("turns").length(); i++) {
            jsonGenerator.writeString(json.getJSONArray("turns").getString(i));
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeArrayFieldStart("board");
        for(int i = 0; i < json.getJSONArray("board").length(); i++) {
            jsonGenerator.writeStartArray();
            for(int j = 0; j < json.getJSONArray("board").getJSONArray(i).length(); j++) {
                jsonGenerator.writeString(json.getJSONArray("board").getJSONArray(i).getString(j));
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
