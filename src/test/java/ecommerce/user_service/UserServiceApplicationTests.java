package ecommerce.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}


//Why contextLoads() needs the DB even though the test doesn't query it
//Your test:
//@SpringBootTest
//class UserServiceApplicationTests {
//
//    @Test
//    void contextLoads() {
//    }
//}
//
//doesn't explicitly call a repository.
//But @SpringBootTest starts essentially your complete Spring Boot application context. Your application has JPA, MySQL, Flyway and Batch.   pom
//During startup Spring will try to create things such as:
//@SpringBootTest
//      ↓
//start Spring application
//      ↓
//configure DataSource
//      ↓
//Hikari connection pool
//      ↓
//connect to MySQL
//      ↓
//Flyway migrations
//      ↓
//JPA/Hibernate
//      ↓
//Spring Batch infrastructure
//      ↓
//ApplicationContext started
//      ↓
//contextLoads() passes
//
//So even though this:
//@Test
//void contextLoads() {
//}
//
//has an empty body, starting the context itself needs your database with the current configuration.
//Your simple:
//new DummyController()
//
//test didn't start Spring at all, which is why it needed no DB.
