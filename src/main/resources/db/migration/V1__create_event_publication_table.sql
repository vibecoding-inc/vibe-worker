-- Spring Modulith Event Publication Registry table
-- This table stores event publications for transactional event listeners
-- See: https://docs.spring.io/spring-modulith/reference/events.html

CREATE TABLE event_publication (
    id UUID PRIMARY KEY,
    completion_date TIMESTAMP,
    event_type VARCHAR(512) NOT NULL,
    listener_id VARCHAR(512) NOT NULL,
    publication_date TIMESTAMP NOT NULL,
    serialized_event TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    completion_attempts INT NOT NULL DEFAULT 0,
    last_resubmission_date TIMESTAMP
);

-- Index for querying incomplete event publications
CREATE INDEX idx_event_publication_completion_date ON event_publication(completion_date);

-- Index for querying by publication date
CREATE INDEX idx_event_publication_publication_date ON event_publication(publication_date);

-- Index for querying by status
CREATE INDEX idx_event_publication_status ON event_publication(status);
