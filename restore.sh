#!/bin/bash

pg_restore -U postgres -d job_board_db /docker-entrypoint-initdb.d/JobBoardBackup.tar
