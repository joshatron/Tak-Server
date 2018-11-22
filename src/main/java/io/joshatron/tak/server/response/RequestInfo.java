package io.joshatron.tak.server.response;

import io.joshatron.tak.engine.game.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestInfo {
    private String username;
    private Player requesterColor;
    private Player first;
    private int size;
}
