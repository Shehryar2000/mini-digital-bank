INSERT INTO cities (name, prefix)
VALUES 
('Karachi', 'KHI'),
('Islamabad', 'ISB'),
('Lahore', 'LHR');

INSERT INTO branches (code, city_id, name, area, created_at, updated_at)
VALUES
('0001', 1, 'Lal Kothi', 'P.E.C.H.S', now(), now()),
('0100', 2, 'Blue Area', 'Secor-F', now(), now()),
('0200', 3, 'Liberty Chowk', 'Gulberg III', now(), now());