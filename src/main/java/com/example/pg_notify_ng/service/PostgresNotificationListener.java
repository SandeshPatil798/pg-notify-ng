package com.example.pg_notify_ng.service;

import com.example.pg_notify_ng.dto.NotificationPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import com.impossibl.postgres.jdbc.PGDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Service
public class PostgresNotificationListener {

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private PGConnection connection;
  private final PGDataSource dataSource;

  @Value("${app.notification.channel}")
  private String notificationChannel;

  // Injecting connection details from application.properties
  public PostgresNotificationListener(@Value("${app.datasource.host}") String host,
      @Value("${app.datasource.port}") int port,
      @Value("${app.datasource.database}") String database,
      @Value("${app.datasource.user}") String user,
      @Value("${app.datasource.password}") String password) {
    // Create a pgjdbc-ng data source for the asynchronous connection
    dataSource = new PGDataSource();
    dataSource.setHost(host);
    dataSource.setPort(port);
    dataSource.setDatabaseName(database);
    dataSource.setUser(user);
    dataSource.setPassword(password);
  }

  @PostConstruct
  public void startListening() {
    try {
      // Get a connection from the pool
      connection = (PGConnection) dataSource.getConnection();
      log.info("Successfully connected to PostgreSQL for notifications.");

      // Add a listener to the connection
      connection.addNotificationListener(new PGNotificationListener() {
        @Override
        // CORRECTED METHOD NAME: notification() instead of onNotification()
        public void notification(int processId, String channelName, String payload) {
          log.info("Received notification on channel '{}'", channelName);
          processPayload(payload);
        }
      });

      // Begin listening on the specified channel
      try (Statement statement = connection.createStatement()) {
        statement.execute(String.format("LISTEN %s", notificationChannel));
        log.info("Successfully listening on channel: {}", notificationChannel);
      }

    } catch (SQLException e) {
      log.error("Failed to start PostgreSQL notification listener", e);
      // Optionally handle reconnection logic here
    }
  }

  private void processPayload(String payload) {
    try {
      NotificationPayload notification = objectMapper.readValue(payload, NotificationPayload.class);

      log.info("Parsed notification payload: Operation='{}', Table='{}.{}'",
          notification.getOperation(), notification.getSchema(), notification.getTable());

      // Log old and new data separately for clarity
      if (notification.getOldData() != null) {
        log.info("Old Data: {}", objectMapper.writeValueAsString(notification.getOldData()));
      }
      if (notification.getNewData() != null) {
        log.info("New Data: {}", objectMapper.writeValueAsString(notification.getNewData()));
      }

    } catch (JsonProcessingException e) {
      log.error("Error parsing notification payload JSON: {}", payload, e);
    }
  }

  @PreDestroy
  public void stopListening() {
    if (connection != null) {
      try {
        // Stop listening to the channel before closing
        try (Statement statement = connection.createStatement()) {
          statement.execute(String.format("UNLISTEN %s", notificationChannel));
          log.info("Stopped listening on channel: {}", notificationChannel);
        }
        // Close the connection
        if (!connection.isClosed()) {
          connection.close();
          log.info("PostgreSQL notification connection closed.");
        }
      } catch (SQLException e) {
        log.error("Error while closing PostgreSQL connection", e);
      }
    }
  }
}