package com.luixtech.uidgenerator.core.id;


import java.time.Instant;

public abstract class IdGenerator {
    private static final ShortIdGenerator     SHORT_ID_GENERATOR      = new ShortIdGenerator();
    /**
     * Generate a thread-safe digit format ID
     *
     * @return 19 bits length，e.g：1672888135850179037
     */
    public static long generateTimestampId() {
        return TimestampIdGenerator.nextId();
    }

    /**
     * Parse the timestampId ID to the approximate timestamp
     *
     * @param timestampId ID
     * @return instant object
     */
    public static Instant parseTimestampId(long timestampId) {
        return TimestampIdGenerator.parseId(timestampId);
    }

    /**
     * Generate a thread-safe digit format ID
     *
     * @return 12 bits length，e.g：306554419571
     */
    public static long generateShortId() {
        return SHORT_ID_GENERATOR.nextId();
    }

    /**
     * Generate a 19 bits format ID
     *
     * @return 19 bits length，e.g：T317297928250941551
     */
    public static String generateTraceId() {
        return "T" + generateTimestampId();
    }
}
