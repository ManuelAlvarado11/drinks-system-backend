package drinks.system.reportingservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Scheduled service that refreshes the reporting summary tables by querying
 * the sales and inventory schemas in the same PostgreSQL database.
 *
 * Frequencies:
 * - Daily sales & inventory status: every 5 minutes
 * - Monthly sales & product ranking: every hour
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportingRefreshService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Refreshes daily_sales_summary for today.
     * Runs every 5 minutes.
     */
    @Scheduled(fixedRateString = "${reporting.refresh.daily-sales-ms:300000}")
    @Transactional
    public void refreshDailySales() {
        log.debug("Refreshing daily sales summary...");
        LocalDate today = LocalDate.now();
        Instant now = Instant.now();

        jdbcTemplate.update("""
            INSERT INTO reporting.daily_sales_summary
                (branch_id, summary_date, total_sales_count, total_revenue, total_discount, total_tax, net_revenue, refreshed_at)
            SELECT
                s.branch_id,
                s.sale_date::date AS summary_date,
                COUNT(*) AS total_sales_count,
                COALESCE(SUM(s.total_amount), 0) AS total_revenue,
                COALESCE(SUM(s.discount_amount), 0) AS total_discount,
                COALESCE(SUM(s.tax_amount), 0) AS total_tax,
                COALESCE(SUM(s.total_amount - s.discount_amount), 0) AS net_revenue,
                ? AS refreshed_at
            FROM sales.sales s
            WHERE s.status = 'COMPLETED'
              AND s.sale_date::date = ?
            GROUP BY s.branch_id, s.sale_date::date
            ON CONFLICT (branch_id, summary_date)
            DO UPDATE SET
                total_sales_count = EXCLUDED.total_sales_count,
                total_revenue = EXCLUDED.total_revenue,
                total_discount = EXCLUDED.total_discount,
                total_tax = EXCLUDED.total_tax,
                net_revenue = EXCLUDED.net_revenue,
                refreshed_at = EXCLUDED.refreshed_at
            """, Timestamp.from(now), java.sql.Date.valueOf(today));

        log.debug("Daily sales summary refreshed for {}", today);
    }

    /**
     * Refreshes monthly_sales_summary for current month.
     * Runs every hour.
     */
    @Scheduled(fixedRateString = "${reporting.refresh.monthly-sales-ms:3600000}")
    @Transactional
    public void refreshMonthlySales() {
        log.debug("Refreshing monthly sales summary...");
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        Instant now = Instant.now();

        jdbcTemplate.update("""
            INSERT INTO reporting.monthly_sales_summary
                (branch_id, year, month, total_sales_count, total_revenue, total_discount, total_tax, net_revenue, refreshed_at)
            SELECT
                s.branch_id,
                EXTRACT(YEAR FROM s.sale_date)::int AS year,
                EXTRACT(MONTH FROM s.sale_date)::int AS month,
                COUNT(*) AS total_sales_count,
                COALESCE(SUM(s.total_amount), 0) AS total_revenue,
                COALESCE(SUM(s.discount_amount), 0) AS total_discount,
                COALESCE(SUM(s.tax_amount), 0) AS total_tax,
                COALESCE(SUM(s.total_amount - s.discount_amount), 0) AS net_revenue,
                ? AS refreshed_at
            FROM sales.sales s
            WHERE s.status = 'COMPLETED'
              AND EXTRACT(YEAR FROM s.sale_date) = ?
              AND EXTRACT(MONTH FROM s.sale_date) = ?
            GROUP BY s.branch_id, EXTRACT(YEAR FROM s.sale_date), EXTRACT(MONTH FROM s.sale_date)
            ON CONFLICT (branch_id, year, month)
            DO UPDATE SET
                total_sales_count = EXCLUDED.total_sales_count,
                total_revenue = EXCLUDED.total_revenue,
                total_discount = EXCLUDED.total_discount,
                total_tax = EXCLUDED.total_tax,
                net_revenue = EXCLUDED.net_revenue,
                refreshed_at = EXCLUDED.refreshed_at
            """, Timestamp.from(now), year, month);

        log.debug("Monthly sales summary refreshed for {}/{}", year, month);
    }

    /**
     * Refreshes product_sales_ranking for today's period.
     * Runs every hour.
     */
    @Scheduled(fixedRateString = "${reporting.refresh.product-ranking-ms:3600000}")
    @Transactional
    public void refreshProductRanking() {
        log.debug("Refreshing product sales ranking...");
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        Instant now = Instant.now();

        // Delete existing rankings for this period then re-insert
        jdbcTemplate.update("""
            DELETE FROM reporting.product_sales_ranking
            WHERE period_start = ? AND period_end = ?
            """, java.sql.Date.valueOf(firstOfMonth), java.sql.Date.valueOf(today));

        jdbcTemplate.update("""
            INSERT INTO reporting.product_sales_ranking
                (product_id, branch_id, product_name, category_name, total_quantity_sold, total_revenue, profit, period_start, period_end, refreshed_at)
            SELECT
                sd.product_id,
                s.branch_id,
                COALESCE(p.name, 'Producto #' || sd.product_id) AS product_name,
                COALESCE(c.name, 'Sin categoría') AS category_name,
                SUM(sd.quantity) AS total_quantity_sold,
                SUM(sd.subtotal) AS total_revenue,
                SUM(sd.subtotal) - SUM(sd.quantity * COALESCE(p.cost_price, 0)) AS profit,
                ? AS period_start,
                ? AS period_end,
                ? AS refreshed_at
            FROM sales.sale_details sd
            JOIN sales.sales s ON s.id = sd.sale_id
            LEFT JOIN inventory.products p ON p.id = sd.product_id
            LEFT JOIN inventory.categories c ON c.id = p.category_id
            WHERE s.status = 'COMPLETED'
              AND s.sale_date::date >= ?
              AND s.sale_date::date <= ?
            GROUP BY sd.product_id, s.branch_id, p.name, c.name
            """, java.sql.Date.valueOf(firstOfMonth), java.sql.Date.valueOf(today), Timestamp.from(now), java.sql.Date.valueOf(firstOfMonth), java.sql.Date.valueOf(today));

        log.debug("Product sales ranking refreshed for period {}/{}", firstOfMonth, today);
    }

    /**
     * Refreshes inventory_status_view from current stock.
     * Runs every 5 minutes.
     */
    @Scheduled(fixedRateString = "${reporting.refresh.inventory-status-ms:300000}")
    @Transactional
    public void refreshInventoryStatus() {
        log.debug("Refreshing inventory status...");
        Instant now = Instant.now();

        // Truncate and re-insert (inventory status is a full snapshot)
        jdbcTemplate.update("DELETE FROM reporting.inventory_status_view");

        jdbcTemplate.update("""
            INSERT INTO reporting.inventory_status_view
                (product_id, branch_id, product_name, category_name, current_stock, minimum_stock, cost_price, sale_price, is_low_stock, refreshed_at)
            SELECT
                ps.product_id,
                ps.branch_id,
                p.name AS product_name,
                COALESCE(c.name, 'Sin categoría') AS category_name,
                ps.current_stock,
                ps.minimum_stock,
                p.cost_price,
                p.sale_price,
                (ps.current_stock <= ps.minimum_stock) AS is_low_stock,
                ? AS refreshed_at
            FROM inventory.product_stock ps
            JOIN inventory.products p ON p.id = ps.product_id
            LEFT JOIN inventory.categories c ON c.id = p.category_id
            WHERE p.is_active = true
              AND p.tracks_inventory = true
            """, Timestamp.from(now));

        log.debug("Inventory status refreshed");
    }
}
