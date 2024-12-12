CREATE TABLE bulkload_dependencies (
    dependencies_id BIGINT,
    depends_id BIGINT
);

CREATE INDEX bulkload_dependencies_dependencies_index ON bulkload_dependencies USING btree (dependencies_id);
CREATE INDEX bulkload_dependencies_depends_index ON bulkload_dependencies USING btree (depends_id);
