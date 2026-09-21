package ecommerce.user_service.service.batch;

import ecommerce.user_service.dto.batch.UserCsvRow;
import ecommerce.user_service.entity.ImportFailureStage;
import ecommerce.user_service.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@StepScope
@Component
@Slf4j
public class UserImportSkipListener implements SkipListener<UserCsvRow, User> {

    private final UserImportRejectionService rejectionService;
    private final Long jobExecutionId;

    public UserImportSkipListener(
            UserImportRejectionService rejectionService,
            @Value("#{stepExecution.jobExecution.id}") Long jobExecutionId) {

        this.rejectionService = rejectionService;
        this.jobExecutionId = jobExecutionId;
    }

//    @Override
//    public void onSkipInRead(Throwable t) {
//        log.warn("User skipped while reading CSV. reason={}",
//                t.getMessage());
//    }
//
//    @Override
//    public void onSkipInProcess(UserCsvRow item, Throwable t) {
//        log.warn(
//                "User skipped while processing. email={}, reason={}",
//                item.email(),
//                t.getMessage()
//        );
//    }
//
//    @Override
//    public void onSkipInWrite(User item, Throwable t) {
//        log.warn(
//                "User skipped while writing. email={}, reason={}",
//                item.getEmail(),
//                t.getMessage()
//        );
//    }

    @Override
    public void onSkipInRead(Throwable throwable) {

        String rawData = null;
        String errorMessage;
//        if (throwable instanceof FlatFileParseException ex) {
//            rawData = ex.getInput();
//        }

        if (throwable instanceof FlatFileParseException ex) {
            rawData = redactPassword(ex.getInput());
            errorMessage = "CSV parsing failed at line " + ex.getLineNumber();
        } else {
            errorMessage = throwable.getMessage();
        }
//        log.warn("User skipped while reading CSV. jobExecutionId={}, reason={}",
//                jobExecutionId,
//                throwable.getMessage()
//        );
        log.warn("User skipped while reading CSV. jobExecutionId={}, reason={}",
                jobExecutionId,
                errorMessage
        );
        rejectionService.saveRejection(
                jobExecutionId,
                ImportFailureStage.READ,
                null,
                rawData,
                throwable,
                errorMessage
        );
    }

    @Override
    public void onSkipInProcess(
            UserCsvRow item,
            Throwable throwable) {

        String rawData = String.join(",",
                safe(item.firstName()),
                safe(item.lastName()),
                safe(item.email()),
                safe(item.phone()),
                "[REDACTED]"
        );

        log.warn("User skipped while processing. jobExecutionId={}, email={}, reason={}",
                jobExecutionId,
                item.email(),
                throwable.getMessage()
        );
        rejectionService.saveRejection(
                jobExecutionId,
                ImportFailureStage.PROCESS,
                item.email(),
                rawData,
                throwable,
                throwable.getMessage());
    }

    @Override
    public void onSkipInWrite(
            User item,
            Throwable throwable) {

        String rawData = String.join(",",
                safe(item.getFirstName()),
                safe(item.getLastName()),
                safe(item.getEmail()),
                safe(item.getPhone()),
                "[REDACTED]"
        );

        log.warn("User skipped while writing. jobExecutionId={}, email={}, reason={}",
                jobExecutionId,
                item.getEmail(),
                throwable.getMessage()
        );

        rejectionService.saveRejection(
                jobExecutionId,
                ImportFailureStage.WRITE,
                item.getEmail(),
                rawData,
                throwable,
                throwable.getMessage()
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String redactPassword(String input) {

        if (input == null) {
            return null;
        }

        String[] fields = input.split(",", -1);

        // Normal CSV has:
        // firstName,lastName,email,phone,password
        if (fields.length >= 5) {
            fields[4] = "[REDACTED]";
        }

        return String.join(",", fields);
    }
}
