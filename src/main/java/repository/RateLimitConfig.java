package repository;

import java.util.concurrent.TimeUnit;

public class RateLimitConfig {

    private int tokens;
    private TimeUnit timeUnit;
    private int capacity;

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit == null ? TimeUnit.SECONDS : timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
