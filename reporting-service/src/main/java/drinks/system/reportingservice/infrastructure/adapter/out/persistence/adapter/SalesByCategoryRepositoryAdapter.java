package drinks.system.reportingservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.reportingservice.application.dto.response.SalesByCategoryResponse;
import drinks.system.reportingservice.domain.port.out.SalesByCategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SalesByCategoryRepositoryAdapter implements SalesByCategoryRepositoryPort {

    private static final String TZ = "America/El_Salvador";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Page<SalesByCategoryResponse> findSalesByCategory(
            Pageable pageable,
            Long branchId,
            LocalDate dateFrom,
            LocalDate dateTo,
            List<Long> categoryIds,
            String groupBy) {

        boolean groupByMonth = "month".equalsIgnoreCase(groupBy);
        boolean filterCategories = categoryIds != null && !categoryIds.isEmpty();

        // Date expression: converts UTC TIMESTAMPTZ → local date or month start
        String localDate = "(s.sale_date AT TIME ZONE '" + TZ + "')::date";
        String dateExpr = groupByMonth
                ? "DATE_TRUNC('month', (s.sale_date AT TIME ZONE '" + TZ + "'))::date"
                : localDate;

        // Build dynamic WHERE clauses and params
        StringBuilder where = new StringBuilder("WHERE s.status = 'COMPLETED'\n");
        List<Object> params = new ArrayList<>();

        if (branchId != null) {
            where.append("  AND s.branch_id = ?\n");
            params.add(branchId);
        }
        if (dateFrom != null) {
            where.append("  AND ").append(localDate).append(" >= ?\n");
            params.add(Date.valueOf(dateFrom));
        }
        if (dateTo != null) {
            where.append("  AND ").append(localDate).append(" <= ?\n");
            params.add(Date.valueOf(dateTo));
        }
        if (filterCategories) {
            String placeholders = "?,".repeat(categoryIds.size()).replaceAll(",$", "");
            where.append("  AND c.id IN (").append(placeholders).append(")\n");
            params.addAll(categoryIds);
        }

        String fromJoins = """
                FROM sales.sale_details sd
                JOIN sales.sales s        ON s.id  = sd.sale_id
                JOIN inventory.products p ON p.id  = sd.product_id
                LEFT JOIN inventory.categories c ON c.id = p.category_id
                """;

        String groupByClause = "GROUP BY c.id, c.name, " + dateExpr;

        String dataQuery = """
                SELECT
                    COALESCE(c.id, 0)                           AS category_id,
                    COALESCE(c.name, 'Sin categoría')           AS category_name,
                    %s                                          AS period_date,
                    COUNT(DISTINCT s.id)                        AS total_sales_count,
                    COALESCE(SUM(sd.subtotal), 0)               AS total_revenue,
                    COALESCE(SUM(sd.discount), 0)               AS total_discount,
                    COALESCE(SUM(sd.subtotal - sd.discount), 0) AS net_revenue
                %s%s
                %s
                ORDER BY period_date DESC, total_revenue DESC
                LIMIT ? OFFSET ?
                """.formatted(dateExpr, fromJoins, where, groupByClause);

        String countQuery = """
                SELECT COUNT(*) FROM (
                    SELECT 1 %s%s%s
                ) sub
                """.formatted(fromJoins, where, groupByClause);

        // Data params = filters + LIMIT + OFFSET
        List<Object> dataParams = new ArrayList<>(params);
        dataParams.add(pageable.getPageSize());
        dataParams.add(pageable.getOffset());

        List<SalesByCategoryResponse> rows = jdbcTemplate.query(
                dataQuery,
                (rs, rowNum) -> new SalesByCategoryResponse(
                        rs.getLong("category_id"),
                        rs.getString("category_name"),
                        rs.getDate("period_date").toLocalDate(),
                        rs.getInt("total_sales_count"),
                        rs.getBigDecimal("total_revenue"),
                        rs.getBigDecimal("total_discount"),
                        rs.getBigDecimal("net_revenue")
                ),
                dataParams.toArray()
        );

        Long total = jdbcTemplate.queryForObject(countQuery, Long.class, params.toArray());

        return new PageImpl<>(rows, pageable, total != null ? total : 0L);
    }
}
