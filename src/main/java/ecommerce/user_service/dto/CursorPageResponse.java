package ecommerce.user_service.dto;

import java.util.List;

public record CursorPageResponse<T, C>(
        List<T> content,
        C nextCursor,
        boolean hasNext
) {
}
