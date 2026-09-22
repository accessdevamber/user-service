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
}