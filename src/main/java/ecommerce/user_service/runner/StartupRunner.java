package ecommerce.user_service.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        log.info("=====StartupRunner run() method called=====");
        log.info("Application current working directory = {}", System.getProperty("user.dir"));

        //Key                 Value
        //
        //user.dir            /Users/ambersingh/Desktop/freelance-ecommerce/user-service
        //user.home           /Users/ambersingh
        //user.name           ambersingh
        //java.version        17....
        //os.name             Mac OS X

//        log.info("user.home : {}", System.getProperty("user.home"));
//        log.info("java.version : {}", System.getProperty("java.version"));
//        log.info("os.name : {}", System.getProperty("os.name"));
    }
}

//System.getProperty("user.dir")
//│                 │
//│                 └── name of the system property
//│
//└── Java's System class
//What is a system property?
//
//When the JVM starts, it has a collection of key/value properties describing the Java process and its environment.
//
//Conceptually:
//
//Key                 Value
//
//user.dir            /Users/ambersingh/Desktop/freelance-ecommerce/user-service
//user.home           /Users/ambersingh
//user.name           ambersingh
//java.version        17....
//os.name             Mac OS X
//
//You retrieve one using:
//
//System.getProperty("property-name");
//
//For example:
//
//System.out.println(System.getProperty("user.dir"));
//System.out.println(System.getProperty("user.home"));
//System.out.println(System.getProperty("java.version"));
//System.out.println(System.getProperty("os.name"));
//Where does user.dir come from?
//
//When the JVM process is started, Java initializes user.dir to represent the process's current working directory.
//
//For example, imagine starting your application manually:
//
//cd /Users/ambersingh/Desktop/freelance-ecommerce/user-service
//
//java -jar target/user-service.jar
//
//At startup, the process is launched with:
//
//Current working directory:
//
/// Users/ambersingh/Desktop/freelance-ecommerce/user-service
//
//and Java exposes that through:
//
//System.getProperty("user.dir")
//
//So you're not setting it anywhere in your Spring code.
//
//It's associated with how the Java process was launched.
//
//In your case IntelliJ launches Java
//
//You're not typing java -jar manually. You press IntelliJ's Run button.
//
//IntelliJ has a Run Configuration roughly like:
//
//Run Configuration: user-service
//
//Main class:       UserServiceApplication
//JDK:              Java 17
//Working directory: /Users/ambersingh/Desktop/freelance-ecommerce/user-service
//Environment vars: DB_URL=...
//                  DB_USERNAME=...
//                  DB_PASSWORD=...
//
//IntelliJ launches the JVM using that working directory.
//
//Then Java can report it:
//
//System.getProperty("user.dir")
//Why this matters for your upload code
//
//Suppose your runner prints:
//
//Application current working directory =
///Users/ambersingh/Desktop/freelance-ecommerce/user-service
//
//Then this:
//
//Paths.get("uploads/user-imports");
//
//is a relative path.
//
//It effectively resolves relative to the working directory:
//
///Users/ambersingh/Desktop/freelance-ecommerce/user-service
//+
//uploads/user-imports
//
//giving:
//
///Users/ambersingh/Desktop/freelance-ecommerce/user-service/uploads/user-imports
//
//That's also why this is useful:
//
//Path path = Paths.get("uploads/user-imports");
//
//System.out.println(path);
//System.out.println(path.toAbsolutePath());
//
//You'd see approximately:
//
//uploads/user-imports
//
///Users/ambersingh/Desktop/freelance-ecommerce/user-service/uploads/user-imports
//
//So the simple mental model is:
//
//IntelliJ / Terminal
//       ↓
//starts Java process from some directory
//       ↓
//JVM has current working directory
//       ↓
//Java exposes it as user.dir
//       ↓
//System.getProperty("user.dir")
//       ↓
//"/Users/ambersingh/Desktop/freelance-ecommerce/user-service"
