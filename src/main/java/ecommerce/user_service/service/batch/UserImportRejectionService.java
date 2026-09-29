package ecommerce.user_service.service.batch;

import ecommerce.user_service.entity.ImportFailureStage;
import ecommerce.user_service.entity.UserImportRejection;
import ecommerce.user_service.repo.UserImportRejectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserImportRejectionService {

    private final UserImportRejectionRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRejection(
            Long jobExecutionId,
            ImportFailureStage stage,
            String email,
            String rawData,
            Throwable exception,
            String errorMessage) {

        UserImportRejection rejection =
                UserImportRejection.builder()
                        .jobExecutionId(jobExecutionId)
                        .stage(stage)
                        .email(email)
                        .rawData(rawData)
                        .errorType(exception.getClass().getSimpleName())
                        //.errorMessage(exception.getMessage())
                        .errorMessage(errorMessage)
                        .build();

        repository.save(rejection);
    }
}