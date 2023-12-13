# Use an official Tomcat image as the base image
FROM tomcat:latest

# Copy your JSP application (WAR file) to the webapps directory
COPY /target/Job_Board_with_Application_Submission.war /usr/local/tomcat/webapps/

# Expose port 8081
EXPOSE 8081

# Set environment variables
ENV JDBC_URL=jdbc:postgresql://db:5432/job_board_db
ENV DBUSER=postgres
ENV PASSWORD=0091

# Start Tomcat with the specified CMD
CMD ["catalina.sh", "run"]
