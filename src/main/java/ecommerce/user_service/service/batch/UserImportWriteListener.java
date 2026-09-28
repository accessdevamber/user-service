package ecommerce.user_service.service.batch;

import ecommerce.user_service.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserImportWriteListener implements ItemWriteListener<User> {

    @Override
    public void onWriteError(Exception exception, Chunk<? extends User> items) {

        if (exception instanceof CannotAcquireLockException) {

            log.warn(
                    "===== USER WRITE DEADLOCK ===== chunkSize={}, thread={}, exception={}, message={}",
                    items.size(),
                    Thread.currentThread().getName(),
                    exception.getClass().getSimpleName(),
                    exception.getMessage()
            );
        }
    }
}