package com.beggar.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSchemaInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final String bootstrapUsername;
    private final String bootstrapPassword;
    private final String bootstrapDisplayName;

    public AdminSchemaInitializer(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            @Value("${admin.bootstrap.username}") String bootstrapUsername,
            @Value("${admin.bootstrap.password}") String bootstrapPassword,
            @Value("${admin.bootstrap.display-name}") String bootstrapDisplayName
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapUsername = bootstrapUsername;
        this.bootstrapPassword = bootstrapPassword;
        this.bootstrapDisplayName = bootstrapDisplayName;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS admin_account (
                    admin_id BIGINT NOT NULL AUTO_INCREMENT,
                    username VARCHAR(50) NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    display_name VARCHAR(50) NOT NULL,
                    role VARCHAR(30) NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (admin_id),
                    UNIQUE KEY uk_admin_account_username (username)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM admin_account WHERE username = ?",
                Integer.class,
                bootstrapUsername
        );

        if (count != null && count == 0) {
            jdbcTemplate.update(
                    """
                    INSERT INTO admin_account
                        (username, password_hash, display_name, role, status)
                    VALUES (?, ?, ?, 'SUPER_ADMIN', 'ACTIVE')
                    """,
                    bootstrapUsername,
                    passwordEncoder.encode(bootstrapPassword),
                    bootstrapDisplayName
            );
        }

        addColumnIfMissing("rooms", "status", "VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'");
        addColumnIfMissing("rooms", "ended_at", "DATETIME NULL");
        addColumnIfMissing("rooms", "deleted_at", "DATETIME NULL");
        jdbcTemplate.update("""
                UPDATE rooms
                   SET status = 'ACTIVE'
                 WHERE status IS NULL
                    OR status = ''
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS good_price_stores (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    store_id VARCHAR(100) NULL,
                    name VARCHAR(150) NOT NULL,
                    category VARCHAR(80) NULL,
                    item_name VARCHAR(150) NULL,
                    price INT NULL,
                    address VARCHAR(200) NOT NULL,
                    lat DECIMAL(10, 7) NULL,
                    lng DECIMAL(10, 7) NULL,
                    phone_number VARCHAR(50) NULL,
                    source VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
                    visible TINYINT(1) NOT NULL DEFAULT 1,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    PRIMARY KEY (id),
                    KEY idx_good_price_stores_visible (visible),
                    KEY idx_good_price_stores_category (category),
                    KEY idx_good_price_stores_address (address)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);

        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS admin_action_logs (
                    log_id BIGINT NOT NULL AUTO_INCREMENT,
                    admin_username VARCHAR(50) NOT NULL,
                    action VARCHAR(50) NOT NULL,
                    target_type VARCHAR(50) NOT NULL,
                    target_id VARCHAR(100) NULL,
                    message VARCHAR(500) NULL,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (log_id),
                    KEY idx_admin_action_logs_admin (admin_username),
                    KEY idx_admin_action_logs_action (action),
                    KEY idx_admin_action_logs_target (target_type, target_id),
                    KEY idx_admin_action_logs_created_at (created_at)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """);
    }

    private void addColumnIfMissing(String tableName, String columnName, String columnDefinition) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                  FROM information_schema.columns
                 WHERE table_schema = DATABASE()
                   AND table_name = ?
                   AND column_name = ?
                """,
                Integer.class,
                tableName,
                columnName
        );

        if (count != null && count == 0) {
            jdbcTemplate.execute("ALTER TABLE %s ADD COLUMN %s %s".formatted(
                    tableName,
                    columnName,
                    columnDefinition
            ));
        }
    }
}
