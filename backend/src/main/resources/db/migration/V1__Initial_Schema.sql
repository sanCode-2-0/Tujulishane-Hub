CREATE TABLE organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    organization_type VARCHAR(255),
    description TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(255),
    address TEXT,
    website_url VARCHAR(255),
    registration_number VARCHAR(255),
    logo_data BYTEA,
    logo_content_type VARCHAR(255),
    approval_status VARCHAR(255),
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    otp VARCHAR(255),
    otp_expiry TIMESTAMP,
    email_verified BOOLEAN NOT NULL,
    email_verification_token VARCHAR(255),
    verified BOOLEAN NOT NULL,
    status VARCHAR(255),
    role VARCHAR(255),
    approval_status VARCHAR(255),
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    last_login TIMESTAMP,
    organization_id BIGINT REFERENCES organizations(id),
    parent_donor_id BIGINT REFERENCES users(id),
    thematic_area VARCHAR(255)
);

CREATE TABLE reviewer_thematic_areas (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    thematic_area VARCHAR(255) NOT NULL,
    assigned_at TIMESTAMP,
    assigned_by BIGINT,
    UNIQUE (user_id, thematic_area)
);

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    partner VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    project_category VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    activity_type TEXT NOT NULL,
    county VARCHAR(255),
    contact_person_name VARCHAR(255) NOT NULL,
    contact_person_role VARCHAR(255) NOT NULL,
    contact_person_email VARCHAR(255),
    objectives TEXT NOT NULL,
    budget NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(255),
    completion_percentage INTEGER,
    completed_at TIMESTAMP,
    has_reports BOOLEAN,
    approval_status VARCHAR(255),
    approval_workflow_status VARCHAR(255),
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason VARCHAR(255),
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP,
    reviewer_comments VARCHAR(255),
    last_modified_by VARCHAR(255),
    last_modified_at TIMESTAMP
);

CREATE TABLE project_theme_assignments (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    project_theme VARCHAR(255) NOT NULL,
    assigned_at TIMESTAMP
);

CREATE TABLE project_locations (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    county VARCHAR(255) NOT NULL,
    sub_county VARCHAR(255),
    maps_address TEXT,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE project_document (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255),
    file_type VARCHAR(255),
    file_size BIGINT,
    data BYTEA,
    project_id BIGINT REFERENCES projects(id),
    uploaded_by_id BIGINT REFERENCES users(id),
    status VARCHAR(255),
    upload_date TIMESTAMP,
    created_at TIMESTAMP
);

CREATE TABLE past_projects (
    id BIGSERIAL PRIMARY KEY,
    partner VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    project_theme VARCHAR(255),
    project_category VARCHAR(255),
    start_date DATE,
    end_date DATE,
    activity_type VARCHAR(255),
    county VARCHAR(255),
    sub_county VARCHAR(255),
    maps_address TEXT,
    contact_person_name VARCHAR(255),
    contact_person_role VARCHAR(255),
    contact_person_email VARCHAR(255),
    objectives TEXT,
    budget NUMERIC(15, 2),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    completed_at TIMESTAMP,
    archived_at TIMESTAMP NOT NULL,
    archived_by VARCHAR(255) NOT NULL,
    final_status VARCHAR(255) NOT NULL,
    completion_percentage INTEGER,
    lessons_learned TEXT,
    success_factors TEXT,
    challenges TEXT,
    recommendations TEXT,
    final_report TEXT
);

CREATE TABLE project_reports (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id),
    past_project_id BIGINT REFERENCES past_projects(id),
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    content TEXT NOT NULL,
    outcomes_achieved TEXT,
    challenges_faced TEXT,
    lessons_learned TEXT,
    recommendations TEXT,
    beneficiaries_reached INTEGER,
    budget_utilized NUMERIC(15, 2),
    budget_variance NUMERIC(15, 2),
    completion_percentage INTEGER,
    attachments TEXT,
    images TEXT,
    report_status VARCHAR(255),
    report_type VARCHAR(255),
    submitted_by BIGINT,
    submitted_at TIMESTAMP,
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP,
    published_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE announcements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    project_id BIGINT NOT NULL REFERENCES projects(id),
    created_by_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(255),
    collaboration_type VARCHAR(255),
    requirements TEXT,
    deadline DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    sender_id BIGINT NOT NULL REFERENCES users(id),
    announcement_id BIGINT NOT NULL REFERENCES announcements(id),
    created_at TIMESTAMP
);