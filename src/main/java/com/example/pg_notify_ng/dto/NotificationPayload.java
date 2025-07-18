package com.example.pg_notify_ng.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class NotificationPayload {
  private OffsetDateTime timestamp;
  private String operation;
  private String schema;
  private String table;

  @JsonProperty("old_data")
  private Map<String, Object> oldData;

  @JsonProperty("new_data")
  private Map<String, Object> newData;
}