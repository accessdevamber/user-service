package ecommerce.user_service.utility;

import java.util.UUID;

public class UuidTest {

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            UUID id = UuidV7Generator.generate();

            System.out.println(id);
            System.out.println("Version: " + id.version());
        }
    }
}