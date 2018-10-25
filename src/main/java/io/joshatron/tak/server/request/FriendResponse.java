package io.joshatron.tak.server.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendResponse {
    private Auth auth;
    private String other;
    private String response;
}
