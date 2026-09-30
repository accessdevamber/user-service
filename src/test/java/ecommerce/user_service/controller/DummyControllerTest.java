package ecommerce.user_service.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DummyControllerTest {

    @Test
    void shouldReturnDummyMessage() {

        DummyController controller = new DummyController();

        String response = controller.dummyMessage();

        assertEquals("Hello from dummy endpoint", response);
    }
}