package ecommerce.user_service.config;

import ecommerce.user_service.dto.batch.UserCsvRow;
import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserRole;
import ecommerce.user_service.entity.UserStatus;
import ecommerce.user_service.exception.InvalidUserImportException;
import ecommerce.user_service.exception.TemporaryUserImportException;
import ecommerce.user_service.service.batch.UserImportSkipListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicInteger;

@EnableJdbcJobRepository
@EnableBatchProcessing(taskExecutorRef = "batchTaskExecutor")
@Configuration
@Slf4j
public class UserImportBatchConfig {

    // ==============================
    // 1. READER
    // ==============================

    @Bean
    public FlatFileItemReader<UserCsvRow> userCsvReader() {

        //Read records from a flat file and produce UserCsvRow objects.
        return new FlatFileItemReaderBuilder<UserCsvRow>()
                .name("userCsvReader")// name of the reader instance
                //.resource(new ClassPathResource("batch/users_10_old.csv"))//inside src/main/resources
                //.resource(new ClassPathResource("batch/users.csv"))//inside src/main/resources
                //.resource(new ClassPathResource("batch/users_10_duplicate_email.csv"))//inside src/main/resources
                //.resource(new ClassPathResource("batch/users_10_existing_db_email.csv"))//inside src/main/resources
                //.resource(new ClassPathResource("batch/users_10_malformed_record.csv"))//inside src/main/resources
                //.resource(new ClassPathResource("batch/users_10_processor_validation_failure.csv"))//inside src/main/resources
                //.resource(new ClassPathResource("batch/users_10_transient_exception_retry.csv"))//inside src/main/resources
                .resource(new ClassPathResource("batch/users_10_async_test.csv"))//inside src/main/resources
                .linesToSkip(1)//The number of lines to skip at the beginning of reading the file.
                //skips:
                //
                //firstName,lastName,email,phone,password
                //
                //because that's our header.
                .delimited()//means fields are separated by a delimiter. Default is comma, which matches our CSV.
                .names(         //names – names of each field
                        "firstName",
                        "lastName",
                        "email",
                        "phone",
                        "password"
                )
                .targetType(UserCsvRow.class)//The class to map to
                .build();
    }

    // ==============================
    // 2. PROCESSOR
    // ==============================

    //Processor converts:
    //
    //UserCsvRow
    //     ↓
    //    User

    //@FunctionalInterface
    //public interface ItemProcessor<I, O>
    //Type parameters:
    //<I> – type of input item
    //<O> – type of output item
    @Bean
    public ItemProcessor<UserCsvRow, User> userProcessor() {

        AtomicInteger vikramAttempts = new AtomicInteger(0);
        return row -> {

            log.info("Processing email={}, thread={}", row.email(), Thread.currentThread().getName());
            Thread.sleep(1000); // TEMP async-learning test

            // Scenario 1: validation failure
            if (row.email() == null || !row.email().contains("@")) {
                log.warn("Invalid email: {}", row.email());
                throw new InvalidUserImportException("Invalid email: " + row.email());
            }

            // Scenario 2: simulated transient failure. succeeds in last attempt/retry attempt
//            if ("batchretry.vikram05@test.com".equals(row.email())) {
//
//                int attempt = vikramAttempts.incrementAndGet();
//                log.info("Processing Vikram. attempt={}", attempt);
//                if (attempt <= 2) {
//                    throw new TemporaryUserImportException("Simulated temporary failure. attempt=" + attempt);
//                }
//            }

            // Scenario 2: simulated transient failure. retries exhausted case
            if ("batchretry.vikram05@test.com".equals(row.email())) {

                int attempt = vikramAttempts.incrementAndGet();
                log.info("Processing Vikram. attempt={}", attempt);
                throw new TemporaryUserImportException("Simulated temporary failure. attempt=" + attempt);
            }

            return User.builder()
                    .firstName(row.firstName())
                    .lastName(row.lastName())
                    .email(row.email())
                    .phone(row.phone())
                    .passwordHash(row.password())
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.CUSTOMER)
                    .build();
        };
    }
    // ==============================
    // 3. WRITER
    // ==============================

    @Bean
    //public JdbcBatchItemWriter<User> userWriter(
    public JdbcBatchItemWriter<User> userJdbcWriter(
            DataSource dataSource) {

        //The names:
        //
        //:firstName
        //:lastName
        //:email
        //...
        //
        //come from the User object's properties.
        //
        //So conceptually:
        //
        //user.getFirstName()
        //
        //feeds:
        //
        //:firstName

        //this dbcBatchItemWriter actually writes to DB
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO users
                        (
                            first_name,
                            last_name,
                            email,
                            phone,
                            password_hash,
                            status,
                            role
                        )
                        VALUES
                        (
                            :firstName,
                            :lastName,
                            :email,
                            :phone,
                            :passwordHash,
                            :status,
                            :role
                        )
                        """)
                .itemSqlParameterSourceProvider(
                        //BeanPropertySqlParameterSource::new
                        //OR
                        //user -> new BeanPropertySqlParameterSource(user)

                        //use below for enum issue for status and role
                        user -> {

                            //log.info("user values : {}", user);
                            MapSqlParameterSource params = new MapSqlParameterSource();
                            params.addValue("firstName", user.getFirstName());
                            params.addValue("lastName", user.getLastName());
                            params.addValue("email", user.getEmail());
                            params.addValue("phone", user.getPhone());
                            params.addValue("passwordHash", user.getPasswordHash());
                            params.addValue("status", user.getStatus().name());
                            params.addValue("role", user.getRole().name());
                            //log.info("MapSqlParameterSource params : {}", params.getValues());
                            return params;
                        }
                )
                .build();
    }

    // ==============================
    // 4. STEP
    // ==============================

    @Bean
    public Step importUserStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<UserCsvRow> userCsvReader,
            ItemProcessor<UserCsvRow, User> userProcessor,
            //JdbcBatchItemWriter<User> userWriter) {
            ItemWriter<User> userWriter,
            UserImportSkipListener skipListener) {

        //This is where Spring Batch becomes different from your existing endpoint:
        //
        //.<UserCsvRow, User>chunk(
        //        3,
        //        transactionManager
        //)
        //
        //It means:
        //
        //INPUT type  = UserCsvRow
        //OUTPUT type = User
        //CHUNK SIZE  = 3
        //
        //With our 10 records:
        //
        //Read user 1
        //Read user 2
        //Read user 3
        //
        //        ↓
        //
        //process 1
        //process 2
        //process 3
        //
        //        ↓
        //
        //WRITE 3
        //COMMIT
        //
        //Then:
        //
        //Read 4
        //Read 5
        //Read 6
        //
        // ↓
        //
        //WRITE 3
        //COMMIT
        //
        //Then:
        //
        //7,8,9
        // ↓
        //WRITE
        //COMMIT
        //
        //Finally:
        //
        //10
        // ↓
        //WRITE
        //COMMIT
        //
        //So:
        //
        //10 records
        //chunk=3
        //
        //3 → commit
        //3 → commit
        //3 → commit
        //1 → commit
        //
        //This is the concept I want you to remember most from this first implementation.
        //
        //For 3 million later:
        //
        //3,000,000
        //chunk=1000
        //
        //1000
        // ↓
        //write
        // ↓
        //commit
        //
        //next 1000
        // ↓
        //write
        // ↓
        //commit
        //
        //...
        //
        //We aren't keeping 3 million users around as one giant application operation.

        /*return new StepBuilder(
                "importUserStep",// the name of the step
                jobRepository//the job repository to which the job should report to
        )
                .<UserCsvRow, User>chunk(3)
                //.<UserCsvRow, User>chunk(10)

                //Params:
                //chunkSize – the chunk size (commit interval)
                //Type parameters:
                //<I> – the type of item to be processed as input
                //<O> – the type of item to be output
                .reader(userCsvReader)// an item reader
                .processor(userProcessor)//an item processor
                .writer(userWriter)// an item writer
                //.skip(DuplicateKeyException.class)
                .build();*/

        log.info("TransactionManager implementation = {}", transactionManager.getClass().getName());
        return new StepBuilder("importUserStep", jobRepository)
                .<UserCsvRow, User>chunk(3)
                .transactionManager(transactionManager)
                .reader(userCsvReader)
                .processor(userProcessor)
                .writer(userWriter)

                // fault tolerance
                .faultTolerant()

                // RETRY
                .retry(TemporaryUserImportException.class)
                .retryLimit(3)

                .skip(DuplicateKeyException.class)
                .skip(FlatFileParseException.class)
                .skip(InvalidUserImportException.class)
                .skip(TemporaryUserImportException.class)
                .skipLimit(10)

                .listener(skipListener)

                .build();
    }

    // ==============================
    // 5. JOB
    // ==============================

    @Bean
    public Job importUserJob(
            JobRepository jobRepository,
            Step importUserStep) {

        return new JobBuilder(
                "importUserJob",//the name of the job
                jobRepository//the job repository to which the job should report to
        )
                .start(importUserStep)
                .build();
    }

    //Easy terminology:
    //
    //JOB
    //└── STEP
    //    ├── Reader
    //    ├── Processor
    //    └── Writer
    //
    //Our job:
    //
    //importUserJob
    //
    //contains:
    //
    //importUserStep
    //
    //and the step does:
    //
    //READ → PROCESS → WRITE
    //
    //Later a job could have several steps:
    //
    //UserImportJob
    //
    //Step 1
    //validate file
    //
    //Step 2
    //import users
    //
    //Step 3
    //generate report
    //
    //But right now: one job, one step.

    //JdbcBatchItemWriter implements ItemWriter
    //this is only for logging chunk size
    @Bean
    public ItemWriter<User> userWriter(JdbcBatchItemWriter<User> userJdbcWriter) {

//        return chunk -> {
//            log.info("Writing chunk. size={}", chunk.size());
//            userJdbcWriter.write(chunk);
//        };
        return chunk -> {

            log.info("Writing chunk. size={}, thread={}", chunk.size(), Thread.currentThread().getName());

            userJdbcWriter.write(chunk);
        };
    }

    @Bean
    public TaskExecutor batchTaskExecutor() {

        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("user-import-");
        return taskExecutor;
    }

}
