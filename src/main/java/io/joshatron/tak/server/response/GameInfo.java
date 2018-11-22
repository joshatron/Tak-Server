package io.joshatron.tak.server.response;

import io.joshatron.tak.engine.game.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class GameInfo {
    private String white;
    private String black;
    private int size;
    private Player first;
    private Date start;
    private Date end;
    private boolean done;
    private String[] turns;
}
