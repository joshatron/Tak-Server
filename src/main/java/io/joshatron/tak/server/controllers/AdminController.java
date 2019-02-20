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

    @PostMapping(value = "/changepass", consumes = "application/json", produces = "application/json")
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
}
