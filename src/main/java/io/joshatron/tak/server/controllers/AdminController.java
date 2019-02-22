package io.joshatron.tak.server.controllers;

import io.joshatron.tak.server.config.SqliteConfig;
import io.joshatron.tak.server.request.Auth;
import io.joshatron.tak.server.request.Text;
import io.joshatron.tak.server.utils.AdminUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin", produces = "application/json")
public class AdminController {

    private AdminUtils adminUtils;
    private Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AdminController() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SqliteConfig.class);
        adminUtils = context.getBean(AdminUtils.class);
    }

    @PostMapping(value = "/initialize")
    public ResponseEntity initialize() {
        try {
            logger.info("Creating admin password");
            String pass = adminUtils.initializeAccount();
            logger.info("Admin password successfully created: {}", pass);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @PostMapping(value = "/changepass", consumes = "application/json")
    public ResponseEntity changePassword(@RequestHeader(value="Authorization") String auth, @RequestBody Text passChange) {
        try {
            logger.info("Changing admin password");
            adminUtils.changePassword(new Auth(auth), passChange);
            logger.info("Admin password successfully changed");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    @PostMapping(value = "/resetuser", consumes = "application/json", produces = "application/json")
    public ResponseEntity resetUserPassword(@RequestHeader(value="Authorization") String auth, @RequestBody Text userToChange) {
        try {
            logger.info("Resetting user password");
            String newPass = adminUtils.resetUserPassword(new Auth(auth), userToChange);
            logger.info("User password successfully reset");
            return new ResponseEntity<>(new Text(newPass), HttpStatus.OK);
        } catch (Exception e) {
            return ControllerUtils.handleExceptions(e, logger);
        }
    }

    //ban user
}
