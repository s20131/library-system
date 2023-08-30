CREATE INDEX available_resources_idx ON resource(id, title, author, release_date, description, series) WHERE status = 'AVAILABLE';

CREATE INDEX author_index_only_idx ON author(id, first_name, last_name);