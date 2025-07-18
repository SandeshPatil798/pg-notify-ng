# pg-notify-ng

## Description

`pg-notify-ng` is a Java application built with Spring Boot that utilizes the pgjdbc-ng driver to handle PostgreSQL's LISTEN/NOTIFY functionality for asynchronous notifications. This project provides a framework for setting up listeners and sending notifications, making it ideal for real-time applications that require database-driven updates.

## Prerequisites

- Java 11 or higher
- PostgreSQL 9.0 or higher
- Gradle 7.x or higher

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/SandeshPatil798/pg-notify-ng.git
   cd pg-notify-ng
   ```

2. Build the project using Gradle:
   ```sh
   ./gradlew build
   ```

3. Run the application:
   ```sh
   ./gradlew bootRun
   ```

## Configuration

Configure the PostgreSQL connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
spring.datasource.username=myuser
spring.datasource.password=mypassword
spring.datasource.driver-class-name=com.impossibl.postgres.jdbc.PGDriver
```

Make sure to replace `mydatabase`, `myuser`, and `mypassword` with your actual database credentials.

## Usage

### Setting up a Listener

To set up a listener for notifications on a specific channel, you can use the following example:

```java
import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class NotificationListener {

    @Autowired
    private DataSource dataSource;

    public void startListening() throws SQLException {
        Connection connection = dataSource.getConnection();
        PGConnection pgConnection = connection.unwrap(PGConnection.class);
        pgConnection.addNotificationListener("my_channel", new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                System.out.println("Received notification on " + channelName + ": " + payload);
            }
        });
    }
}
```

### Sending a Notification

To send a notification to a channel, you can execute the NOTIFY command:

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void sendNotification(String channel, String payload) {
        jdbcTemplate.execute("NOTIFY " + channel + ", '" + payload + "'");
    }
}
```

## Contributing

Contributions to `pg-notify-ng` are welcome! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Submit a pull request with a clear description of your changes.

Please ensure your code follows the project's coding standards and includes tests where applicable.
