package drinks.system.common.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic wrapper for paginated responses.
 * Provides a consistent pagination structure across all microservices.
 *
 * @param <T> the type of elements in the page content
 */
@Builder
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    /**
     * Creates a PageResponse from a Spring Data Page and a mapped content list.
     * Use this when the content has been mapped to DTOs from the Page's entities.
     *
     * @param springPage the Spring Data Page containing pagination metadata
     * @param content    the mapped content list (e.g., DTOs)
     * @param <T>        the type of elements in the content
     * @return a PageResponse with pagination metadata from the Spring Page
     */
    public static <T> PageResponse<T> of(Page<?> springPage, List<T> content) {
        return PageResponse.<T>builder()
                .content(content)
                .page(springPage.getNumber())
                .size(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .first(springPage.isFirst())
                .last(springPage.isLast())
                .build();
    }
}
