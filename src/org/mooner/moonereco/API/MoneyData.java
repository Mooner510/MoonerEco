package org.mooner.moonereco.API;

import java.util.UUID;

public class MoneyData {
    private final UUID uuid;
    private final double value;

    public MoneyData(String uuid, double value) {
        this.uuid = UUID.fromString(uuid);
        this.value = value;
    }

    public UUID getUUID() {
        return uuid;
    }

    public double getValue() {
        return value;
    }
}
