package org.mooner.moonereco.API;

import java.util.UUID;

public class LogData {

    private final LogType source;
    private final UUID uuid;
    private final UUID to;
    private final double value;
    private final long timestamp;

    public LogData(UUID uuid, String to, String source, double value, long timestamp) {
        this.uuid = uuid;
        this.to = UUID.fromString(to);
        this.source = LogType.valueOf(source);
        this.value = value;
        this.timestamp = timestamp;
    }

    public LogData(UUID uuid, String to, LogType source, double value, long timestamp) {
        this.uuid = uuid;
        this.to = UUID.fromString(to);
        this.source = source;
        this.value = value;
        this.timestamp = timestamp;
    }

    public LogData(String uuid, UUID to, String source, double value, long timestamp) {
        this.uuid = UUID.fromString(uuid);
        this.to = to;
        this.source = LogType.valueOf(source);
        this.value = value;
        this.timestamp = timestamp;
    }

    public LogData(String uuid, UUID to, LogType source, double value, long timestamp) {
        this.uuid = UUID.fromString(uuid);
        this.to = to;
        this.source = source;
        this.value = value;
        this.timestamp = timestamp;
    }

    public LogData(UUID uuid, UUID to, String source, double value, long timestamp) {
        this.uuid = uuid;
        this.to = to;
        this.source = LogType.valueOf(source);
        this.value = value;
        this.timestamp = timestamp;
    }

    public LogData(UUID uuid, UUID to, LogType source, double value, long timestamp) {
        this.uuid = uuid;
        this.to = to;
        this.source = source;
        this.value = value;
        this.timestamp = timestamp;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public UUID getTo() {
        return this.to;
    }

    public LogType getSource() {
        return source;
    }

    public double getValue() {
        return this.value;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
