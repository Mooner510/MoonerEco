package Mooner.API;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MoneyLogData {
    private final UUID uuid;
    private final String source;
    private final double value;

    public MoneyLogData(UUID uuid, String source, double value) {
        this.uuid = uuid;
        this.source = source;
        this.value = value;
    }
}
