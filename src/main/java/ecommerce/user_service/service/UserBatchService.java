package ecommerce.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBatchService {

    private final JobOperator jobOperator;
    private final Job importUserJob;
    private final JobRepository jobRepository;

    public JobExecution importUsers() throws Exception {

        //Why timestamp?
        //
        //Spring Batch identifies job instances using:
        //
        //Job name
        //+
        //identifying JobParameters
        //
        //If we run the exact same completed job with identical identifying parameters again, Spring Batch can say:
        //
        //That job instance already completed.
        //
        //So for this experiment:
        //
        //System.currentTimeMillis()
        //
        //makes every request a new job instance.
        //
        //We'll make the parameters more meaningful later, such as an importId or file name/hash.
        log.info("JobRepository implementation = {}", jobRepository.getClass().getName());

        log.info("JobOperator implementation = {}", jobOperator.getClass().getName());
        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong(
                                "timestamp",
                                System.currentTimeMillis()
                        )
                        .toJobParameters();

        log.info("Starting user import batch job");

        log.info("Before starting job. thread={}", Thread.currentThread().getName());
        JobExecution jobExecution = jobOperator.start(
                importUserJob,
                jobParameters
        );

        log.info("User import job executionId={}, status={}",
                jobExecution.getId(),
                jobExecution.getStatus());

        return jobExecution;
    }

    public JobExecution importUsersMultipartFile(MultipartFile file)
            throws Exception {

        Path uploadDirectory = Paths.get("uploads/user-imports");
        log.info("uploadDirectory : {}", uploadDirectory);

//        Path uploadDirectory = Paths.get("uploads/user-imports")
//                .toAbsolutePath();

        log.info("uploadDirectory : {}", uploadDirectory);
        log.info("uploadDirectory absolute = {}", uploadDirectory.toAbsolutePath());


        //working directory -> /Users/ambersingh/Desktop/freelance-ecommerce/user-service
        //source directory -> /Users/ambersingh/Desktop/freelance-ecommerce/user-service/src

        Path createdDirectory = Files.createDirectories(uploadDirectory);
        log.info("createdDirectory : {}", createdDirectory);

        String storedFileName = UUID.randomUUID() + ".csv";
        log.info("storedFileName : {}", storedFileName);

        Path storedFilePath = uploadDirectory
                        .resolve(storedFileName)
                        .toAbsolutePath();
        log.info("storedFilePath : {}", storedFilePath);

        //Transfer the received file to the given destination file.
        file.transferTo(storedFilePath);

        log.info("Uploaded CSV saved to {}", storedFilePath);

        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong(
                                "timestamp",
                                System.currentTimeMillis()
                        )
                        .addString(
                                "inputFile",
                                storedFilePath.toString()
                        )
                        .toJobParameters();

        log.info("Starting user import. inputFile={}, thread={}",
                storedFilePath,
                Thread.currentThread().getName()
        );

        JobExecution execution =
                jobOperator.start(
                        importUserJob,
                        jobParameters
                );

        log.info("Import submitted. executionId={}, status={}",
                execution.getId(),
                execution.getStatus()
        );

        return execution;
    }
}