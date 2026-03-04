package org.ever._4ever_be_gw.common.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Minimal UUID v7 generator (RFC 9562) using currentTimeMillis and randomness.
 * Generates time-ordered UUIDs without external dependencies.
 */
public final class UuidV7 {
    private static final SecureRandom RNG = new SecureRandom();

    private UuidV7() {}

    public static UUID randomUuidV7() {
        long ms = System.currentTimeMillis();
        long hi = (ms << 4) | 0x7L; // 60-bit timestamp + version 7
        long lo = RNG.nextLong();
        lo = (lo & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L; // variant 10xxxxxx
        return new UUID(hi, lo);
    }

    public static String string() {
        return randomUuidV7().toString();
    }
}

