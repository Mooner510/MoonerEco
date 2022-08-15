package org.mooner.moonereco.API;

import java.util.UUID;

public class LogData {

    private final LogType source;
    private final UUID uuid;
    private final UUID to;
    private final String data;
    private final double value;
    private final long timestamp;

    public LogData(UUID uuid, String to, LogType source, String data, double value, long timestamp) {
        this(uuid, UUID.fromString(to), source, data, value, timestamp);
    }

    public LogData(String uuid, UUID to, LogType source, String data, double value, long timestamp) {
        this(UUID.fromString(uuid), to, source, data, value, timestamp);
    }

    public LogData(UUID uuid, UUID to, String source, String data, double value, long timestamp) {
        this(uuid, to, LogType.valueOf(source), data, value, timestamp);
    }

    public LogData(UUID uuid, UUID to, LogType source, String data, double value, long timestamp) {
        this.uuid = uuid;
        this.to = to;
        this.source = source;
        this.data = data;
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

    public String getData() {
        return data;
    }

    public double getValue() {
        return this.value;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
