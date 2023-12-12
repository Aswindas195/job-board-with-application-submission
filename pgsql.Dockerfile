ARG PGTAG=latest
FROM postgres:$PGTAG
# Copy the dump file into the container
COPY jobBoardBackup.dump /docker-entrypoint-initdb.d/
# Expose the default PostgreSQL port (5432)
EXPOSE 5432