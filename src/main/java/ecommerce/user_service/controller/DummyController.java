package ecommerce.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
@Slf4j
public class DummyController {

    @GetMapping()
    String dummyMessage() {
        log.info("Hello from dummy endpoint");
        return "Hello from dummy endpoint";
    }
}
