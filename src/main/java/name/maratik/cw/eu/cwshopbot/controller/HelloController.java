package name.maratik.cw.eu.cwshopbot.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger logger = LogManager.getLogger(HelloController.class);

    @GetMapping("/")
    public String index() {
        logger.info("Index is called");
        return "Hello, world!";
    }
}
