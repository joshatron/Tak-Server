package io.joshatron.tak.server.controllers;

import io.joshatron.tak.server.config.ApplicationConfig;
import io.joshatron.tak.server.request.*;
import io.joshatron.tak.server.response.*;
import io.joshatron.tak.server.utils.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private GameUtils gameUtils;
    private Logger logger = LoggerFactory.getLogger(AccountController.class);

    public GameController() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        gameUtils = context.getBean(GameUtils.class);
    }

    public GameController(GameUtils gameUtils) {
        this.gameUtils = gameUtils;
    }

    @PostMapping(value = "/request/create/{id}", produces = "application/json")
    public ResponseEntity requestGame(@RequestHeader(value="Authorization") String auth, @PathVariable("id") String other, @RequestBody GameRequest gameRequest) {
        try {
            logger.info("Requesting new game");
            gameRequest.setOpponent(other);
            gameRequest.setAuth(new Auth(auth));
            gameUtils.requestGame(gameRequest);
            logger.info("Request successfully made");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @DeleteMapping(value = "/request/cancel/{id}", produces = "application/json")
    public ResponseEntity cancelGameRequest(@RequestHeader(value="Authorization") String auth, @PathVariable("id") String other) {
        try {
            logger.info("Deleting game request");
            gameUtils.deleteRequest(new UserInteraction(new Auth(auth), other));
            logger.info("Successfully deleted request");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @PostMapping(value = "/request/respond/{id}/{answer}", produces = "application/json")
    public ResponseEntity respondToGameRequest(@RequestHeader(value="Authorization") String auth, @PathVariable("id") String id, @PathVariable("answer") String answer) {
        try {
            logger.info("Responding to game request");
            gameUtils.respondToGame(new Answer(new Auth(auth), id, answer));
            logger.info("Successfully responded to game request");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @GetMapping(value = "/request/incoming", produces = "application/json")
    public ResponseEntity checkIncomingGames(@RequestHeader(value="Authorization") String auth) {
        try {
            logger.info("Requesting incoming games");
            RequestInfo[] games = gameUtils.checkIncomingRequests(new Auth(auth));
            logger.info("Incoming games found");
            return new ResponseEntity<>(new GameRequests(games), HttpStatus.OK);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @GetMapping(value = "/request/outgoing", produces = "application/json")
    public ResponseEntity checkOutgoingGames(@RequestHeader(value="Authorization") String auth) {
        try {
            logger.info("Requesting outgoing games");
            RequestInfo[] games = gameUtils.checkOutgoingRequests(new Auth(auth));
            logger.info("Outgoing games found");
            return new ResponseEntity<>(new GameRequests(games), HttpStatus.OK);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @PostMapping(value = "/request/random/create/{size}", produces = "application/json")
    public ResponseEntity requestRandomGame(@RequestHeader(value="Authorization") String auth, @PathVariable("size") int size) {
        try {
            logger.info("Requesting a random game");
            gameUtils.requestRandomGame(new RandomGame(new Auth(auth), size));
            logger.info("Random game successfully made");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @DeleteMapping(value = "/request/random/cancel/{size}", produces = "application/json")
    public ResponseEntity cancelRandomGameRequest(@RequestHeader(value="Authorization") String auth, @PathVariable("size") int size) {
        try {
            logger.info("Deleting random game request");
            gameUtils.deleteRandomRequest(new RandomGame(new Auth(auth), size));
            logger.info("Successfully deleted random request");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @GetMapping(value = "/request/random/outgoing", produces = "application/json")
    public ResponseEntity getOutgoingRandomRequests(@RequestHeader(value="Authorization") String auth) {
        try {
            logger.info("Getting outgoing random request sizes");
            int[] sizes = gameUtils.checkRandomSizes(new Auth(auth));
            logger.info("Outing random games found");
            return new ResponseEntity<>(new RandomSizes(sizes), HttpStatus.OK);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity findGames(@RequestHeader(value="Authorization") String auth, @RequestParam(value = "opponents", required = false) String opponents,
                                    @RequestParam(value = "start", required = false) long start, @RequestParam(value = "end", required = false) long end,
                                    @RequestParam(value = "complete", required = false) boolean complete, @RequestParam(value = "pending", required = false) boolean pending,
                                    @RequestParam(value = "sizes", required = false) String sizes, @RequestParam(value = "winner", required = false) String winner,
                                    @RequestParam(value = "color", required = false) String color) {
        return null;
    }

    @GetMapping(value = "/game/{id}", produces = "application/json")
    public ResponseEntity getGameInfo(@RequestHeader(value="Authorization") String auth, @PathVariable("id") String gameId) {
        return null;
    }

    @GetMapping(value = "/game/{id}/turns", produces = "application/json")
    public ResponseEntity getPossibleTurns(@RequestHeader(value="Authorization") String auth, @PathVariable("id") String gameId) {
        return null;
    }

    @PostMapping(value = "/game/{id}/play", produces = "application/json")
    public ResponseEntity playTurn(@RequestHeader(value="Authorization") String auth, @PathVariable("id") String gameId, @RequestBody PlayTurn turn) {
        return null;
    }

    @GetMapping(value = "/notifications", produces = "application/json")
    public ResponseEntity getNotifications(@RequestHeader(value="Authorization") String auth) {
        return null;
    }
}
