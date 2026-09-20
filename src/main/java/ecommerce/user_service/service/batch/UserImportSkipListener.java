package ecommerce.user_service.service.batch;

import ecommerce.user_service.dto.batch.UserCsvRow;
import ecommerce.user_service.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserImportSkipListener implements SkipListener<UserCsvRow, User> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("User skipped while reading CSV. reason={}",
                t.getMessage());
    }

    @Override
    public void onSkipInProcess(UserCsvRow item, Throwable t) {
        log.warn(
                "User skipped while processing. email={}, reason={}",
                item.email(),
                t.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(User item, Throwable t) {
        log.warn(
                "User skipped while writing. email={}, reason={}",
                item.getEmail(),
                t.getMessage()
        );
    }
}
