package io.joshatron.tak.server.utils;

import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.GameResult;
import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.game.Player;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnUtils;
import io.joshatron.tak.server.database.AccountDAO;
import io.joshatron.tak.server.database.GameDAO;
import io.joshatron.tak.server.database.SocialDAO;
import io.joshatron.tak.server.exceptions.ErrorCode;
import io.joshatron.tak.server.exceptions.GameServerException;
import io.joshatron.tak.server.request.*;
import io.joshatron.tak.server.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class GameUtils {

    @Autowired
    private GameDAO gameDAO;
    @Autowired
    private SocialDAO socialDAO;
    @Autowired
    private AccountDAO accountDAO;

    @Value("${game.forfeit-days:0}")
    private Integer daysToForfeit;

    public void requestGame(Auth auth, String other,GameRequest gameRequest) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, IdUtils.USER_LENGTH);
        Validator.validateGameBoardSize(gameRequest.getSize());
        Player requesterColor = Validator.validatePlayer(gameRequest.getRequesterColor());
        Player first = Validator.validatePlayer(gameRequest.getFirst());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!socialDAO.areFriends(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.ALREADY_FRIENDS);
        }
        if(gameDAO.playingGame(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.GAME_EXISTS);
        }
        if(gameDAO.gameRequestExists(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.GAME_REQUEST_EXISTS);
        }

        gameDAO.createGameRequest(user.getUserId(), other, gameRequest.getSize(), requesterColor, first);
    }

    public void deleteRequest(Auth auth, String other) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(other, IdUtils.USER_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(other)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!gameDAO.gameRequestExists(user.getUserId(), other)) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        gameDAO.deleteGameRequest(user.getUserId(), other);
    }

    public void respondToGame(Auth auth, String id, Text answer) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(id, IdUtils.USER_LENGTH);
        Validator.validateText(answer);
        Answer response = Validator.validateAnswer(answer.getText());
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!accountDAO.userExists(id)) {
            throw new GameServerException(ErrorCode.USER_NOT_FOUND);
        }
        if(!gameDAO.gameRequestExists(id, user.getUserId())) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        if(response == Answer.ACCEPT) {
            RequestInfo info = gameDAO.getGameRequestInfo(id, user.getUserId());
            gameDAO.startGame(id, user.getUserId(), info.getSize(), info.getRequesterColor(), info.getFirst());
        }
        gameDAO.deleteGameRequest(id, user.getUserId());
    }

    public RequestInfo[] checkIncomingRequests(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return gameDAO.getIncomingGameRequests(user.getUserId());
    }

    public RequestInfo[] checkOutgoingRequests(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());

        return gameDAO.getOutgoingGameRequests(user.getUserId());
    }

    public void requestRandomGame(Auth auth, int size) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateGameBoardSize(size);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(gameDAO.randomGameRequestExists(user.getUserId())) {
            throw new GameServerException(ErrorCode.GAME_REQUEST_EXISTS);
        }

        gameDAO.createRandomGameRequest(user.getUserId(), size);
        resolveRandomGameRequests();
    }

    private void resolveRandomGameRequests() throws GameServerException {
        RandomRequestInfo[] requests = gameDAO.getRandomGameRequests();

        for(int i = 0; i < requests.length; i++) {
            if(requests[i] != null) {
                for(int j = i + 1; j < requests.length; j++) {
                    if(requests[j] != null && requests[i].getSize() == requests[j].getSize() &&
                       !gameDAO.playingGame(requests[i].getRequester(), requests[j].getRequester()) &&
                       !socialDAO.isBlocked(requests[i].getRequester(), requests[j].getRequester()) &&
                       !socialDAO.isBlocked(requests[j].getRequester(), requests[i].getRequester())) {
                        gameDAO.startGame(requests[i].getRequester(), requests[j].getRequester(), requests[i].getSize(), Player.WHITE, Player.WHITE);
                        gameDAO.deleteRandomGameRequest(requests[i].getRequester());
                        gameDAO.deleteRandomGameRequest(requests[j].getRequester());
                        requests[j] = null;
                        break;
                    }
                }
            }
        }
    }

    public void deleteRandomRequest(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!gameDAO.randomGameRequestExists(user.getUserId())) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        gameDAO.deleteRandomGameRequest(user.getUserId());
    }

    public int checkRandomSize(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        if(!gameDAO.randomGameRequestExists(user.getUserId())) {
            throw new GameServerException(ErrorCode.REQUEST_NOT_FOUND);
        }

        return gameDAO.getOutgoingRandomRequestSize(user.getUserId());
    }

    public GameInfo getGameInfo(Auth auth, String gameId, Boolean fullState) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(gameId, IdUtils.GAME_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        checkForForfeits(user.getUserId());
        if(!gameDAO.gameExists(gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(!gameDAO.userAuthorizedForGame(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }

        GameInfo info = gameDAO.getGameInfo(gameId);
        info.setMessages(socialDAO.listMessages(user.getUserId(), new String[]{gameId}, null, null, null, null, RecipientType.GAME));
        for(Message message : info.getMessages()) {
            if(!message.getSender().equalsIgnoreCase(user.getUserId())) {
                socialDAO.markMessageRead(message.getId());
                message.setOpened(true);
            }
        }

        if(fullState == null || !fullState) {
            return info;
        }
        else {
            try {
                GameState state = new GameState(info.getFirst(), info.getSize());
                for(String turn : info.getTurns()) {
                    state.executeTurn(TurnUtils.turnFromString(turn));
                }

                info.setFullState(state);

                return info;
            } catch(TakEngineException e) {
                throw new GameServerException(ErrorCode.GAME_ENGINE_ERROR);
            }
        }
    }

    public GameInfo[] findGames(Auth auth, String opponents, Long startTime, Long endTime, String complete, String pending, String sizes, String winner, String color) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        checkForForfeits(user.getUserId());
        Date now = new Date();
        Date start = null;
        if(startTime != null) {
            start = new Date(startTime.longValue());
            if(now.before(start)) {
                throw new GameServerException(ErrorCode.INVALID_DATE);
            }
        }
        Date end = null;
        if(endTime != null) {
            end = new Date(endTime.longValue());
            if(now.before(end)) {
                throw new GameServerException(ErrorCode.INVALID_DATE);
            }
        }
        String[] users = null;
        if(opponents != null && opponents.length() > 0) {
            users = opponents.split(",");
            for (String u : users) {
                Validator.validateId(u, IdUtils.USER_LENGTH);
                if (!accountDAO.userExists(u)) {
                    throw new GameServerException(ErrorCode.USER_NOT_FOUND);
                }
            }
        }
        if(start != null && end != null && start.after(end)) {
            throw new GameServerException(ErrorCode.INVALID_DATE);
        }
        Complete cpt = Validator.validateComplete(complete);
        Pending pnd = Validator.validatePending(pending);
        int[] szs = null;
        if(sizes != null && sizes.length() > 0) {
            String[] all = sizes.split(",");
            szs = new int[all.length];
            for(int i = 0; i < all.length; i++) {
                int size = Integer.parseInt(all[i]);
                Validator.validateGameBoardSize(size);
                szs[i] = size;
            }
        }
        Winner wnr = Validator.validateWinner(winner);
        Player clr = Validator.validatePlayer(color);

        return gameDAO.listGames(user.getUserId(), users, start, end, cpt, pnd, szs, wnr, clr);
    }

    public void sendGameMessage(Auth auth, String gameId, Text message) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(gameId, IdUtils.GAME_LENGTH);
        Validator.validateText(message);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        checkForForfeits(user.getUserId());
        if(!gameDAO.gameExists(gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(!gameDAO.userAuthorizedForGame(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(gameDAO.getGameInfo(gameId).isDone()) {
            throw new GameServerException(ErrorCode.GAME_IS_COMPLETE);
        }

        socialDAO.sendMessage(user.getUserId(), gameId, message.getText(), RecipientType.GAME);
    }

    public String[] getPossibleTurns(Auth auth, String gameId) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(gameId, IdUtils.GAME_LENGTH);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        checkForForfeits(user.getUserId());
        if(!gameDAO.gameExists(gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(!gameDAO.userAuthorizedForGame(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(gameDAO.getGameInfo(gameId).isDone()) {
            return new String[0];
        }
        if(!gameDAO.isYourTurn(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.NOT_YOUR_TURN);
        }

        GameState state = getStateFromId(gameId);

        List<Turn> possible = state.getPossibleTurns();
        String[] toReturn = new String[possible.size()];
        for(int i = 0; i < toReturn.length; i++) {
            toReturn[i] = possible.get(i).toString();
        }

        return toReturn;
    }

    public void playTurn(Auth auth, String gameId, Text turn) throws GameServerException {
        Validator.validateAuth(auth);
        Validator.validateId(gameId, IdUtils.GAME_LENGTH);
        Validator.validateText(turn);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        checkForForfeits(user.getUserId());
        if(!gameDAO.gameExists(gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(!gameDAO.userAuthorizedForGame(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.GAME_NOT_FOUND);
        }
        if(!gameDAO.isYourTurn(user.getUserId(), gameId)) {
            throw new GameServerException(ErrorCode.NOT_YOUR_TURN);
        }

        GameState state = getStateFromId(gameId);
        Turn proposed = TurnUtils.turnFromString(turn.getText());
        if(proposed == null) {
            throw new GameServerException(ErrorCode.INVALID_FORMATTING);
        }

        try {
            state.executeTurn(proposed);
        }
        catch(TakEngineException e) {
            throw new GameServerException(ErrorCode.ILLEGAL_MOVE);
        }

        gameDAO.addTurn(gameId, turn.getText());

        GameResult result = state.checkForWinner();
        if(result.isFinished()) {
            gameDAO.finishGame(gameId, result.getWinner());
            GameInfo info = gameDAO.getGameInfo(gameId);
            if(info.getWinner() == Player.WHITE) {
                updateRatings(info.getWhite(), info.getBlack());
            }
            else {
                updateRatings(info.getBlack(), info.getWhite());
            }
        }
    }

    private void updateRatings(String winner, String loser) throws GameServerException {
        int k = 20;
        User w = accountDAO.getUserFromId(winner);
        User l = accountDAO.getUserFromId(loser);

        //Implement elo diff
        double winnerExpected = 1. / (1 + Math.pow(10, (l.getRating() - w.getRating()) / 400.));
        double loserExpected = 1. / (1 + Math.pow(10, (w.getRating() - l.getRating()) / 400.));

        accountDAO.updateRating(winner, (int)Math.round(w.getRating() + k * (1 - winnerExpected)));
        accountDAO.updateRating(loser, (int)Math.round(l.getRating() + k * (0 - loserExpected)));
    }

    public GameNotifications getNotifications(Auth auth) throws GameServerException {
        Validator.validateAuth(auth);
        if(!accountDAO.isAuthenticated(auth)) {
            throw new GameServerException(ErrorCode.INCORRECT_AUTH);
        }
        User user = accountDAO.getUserFromUsername(auth.getUsername());
        checkForForfeits(user.getUserId());

        return gameDAO.getGameNotifications(user.getUserId());
    }

    private GameState getStateFromId(String gameId) throws GameServerException {
        try {
            GameInfo gameInfo = gameDAO.getGameInfo(gameId);

            Player player = gameInfo.getFirst();
            GameState state = new GameState(player, gameInfo.getSize());
            for(String turn : gameInfo.getTurns()) {
                Turn toPlay = TurnUtils.turnFromString(turn);
                state.executeTurn(toPlay);
            }

            return state;
        }
        catch(TakEngineException e) {
            throw new GameServerException(ErrorCode.GAME_ENGINE_ERROR);
        }
    }

    private void checkForForfeits(String userId) throws GameServerException {
        if(daysToForfeit > 0) {
            GameInfo[] openGames = gameDAO.listGames(userId, null, null, null, Complete.INCOMPLETE, null, null, null, null);

            for(GameInfo game : openGames) {
                //check if it has been enough days
                if(Instant.now().toEpochMilli() - game.getLast() > daysToForfeit * (1000 * 60 * 60 * 24)) {
                    gameDAO.finishGame(game.getGameId(), game.getCurrent().opposite());
                }
            }
        }
    }
}
