CREATE TABLE _user (
    id UUID PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(50)
);
INSERT INTO _user (id, username, email, password, role) VALUES
    ('126422c9-592e-4b65-8b84-f14dcdac7cef', 'admin', 'admin@admin.com', '$2a$10$ugP60filSKHllz6annt7ue9Qp.w1In5EiNtib4MX8lrb9030.daKy', 'ROLE_ADMIN'),
    ('6a971fb8-48b3-4b60-8248-a5d60c7e421c', 'user', 'user@user.com', '$2a$10$QL./UnqTh77.ptg1ezSOGurWSoW725oKiUVm6s9.F/MTUR/fiM7wy', 'ROLE_USER');
