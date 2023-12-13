ARG PGTAG=latest
FROM postgres:$PGTAG

# Copy the dump file into the container
COPY JobBoardBackup.tar /docker-entrypoint-initdb.d/

# Change working directory
WORKDIR /docker-entrypoint-initdb.d/

# Restore the DB
#RUN pg_restore -U postgres -d job_board_db /docker-entrypoint-initdb.d/JobBoardBackup.tar

# Expose the default PostgreSQL port (5432)
EXPOSE 5432