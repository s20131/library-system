CREATE INDEX available_resources_idx ON resource(id, title, author, release_date, description, series) WHERE status = 'AVAILABLE';
