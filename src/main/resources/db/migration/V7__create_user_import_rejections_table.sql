CREATE TABLE user_import_rejections (
    id BIGINT NOT NULL AUTO_INCREMENT,
    job_execution_id BIGINT NOT NULL,
    stage VARCHAR(20) NOT NULL,
    email VARCHAR(150),
    raw_data TEXT,
    error_type VARCHAR(255) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    INDEX idx_import_rejection_job_stage (job_execution_id, stage)
);