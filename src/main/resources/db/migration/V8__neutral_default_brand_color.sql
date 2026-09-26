ALTER TABLE business ALTER COLUMN brand_color SET DEFAULT '#111111';

UPDATE business SET brand_color = '#111111' WHERE brand_color = '#6D5DF6';
