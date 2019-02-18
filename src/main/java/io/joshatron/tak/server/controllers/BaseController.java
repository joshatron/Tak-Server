package io.joshatron.tak.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @Autowired
    private Environment env;

    @GetMapping(value = "/health")
    public ResponseEntity healthCheck() {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/version")
    public ResponseEntity versionCheck() {
        return new ResponseEntity<>(env.getProperty("project.version"), HttpStatus.OK);
    }
}
