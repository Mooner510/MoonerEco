package Mooner.API;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PayLogData {
    private final UUID from;
    private final UUID to;
    private final double value;

    public PayLogData(UUID from, String to, double value) {
        this.from = from;
        this.to = UUID.fromString(to);
        this.value = value;
    }

    public PayLogData(String from, UUID to, double value) {
        this.from = UUID.fromString(from);
        this.to = to;
        this.value = value;
    }

    public PayLogData(UUID from, UUID to, double value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }
}
