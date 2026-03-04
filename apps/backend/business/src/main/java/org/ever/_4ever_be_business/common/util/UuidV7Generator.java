package org.ever._4ever_be_business.common.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

/**
 * UUIDv7 생성 유틸리티 클래스
 * RFC 9562 기반 UUIDv7 구현
 * 시간 기반 정렬 가능한 UUID 생성
 */
public class UuidV7Generator {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * UUIDv7 생성
     * 타임스탬프 기반으로 정렬 가능한 UUID를 생성합니다.
     *
     * @return UUIDv7
     */
    public static String generate() {
        // 현재 시간의 밀리초 타임스탬프
        long timestamp = Instant.now().toEpochMilli();

        // 48비트 타임스탬프 (밀리초)
        long timestampMs = timestamp & 0xFFFF_FFFF_FFFFL;

        // 12비트 랜덤 데이터 (서브-밀리초 정밀도)
        int randomA = RANDOM.nextInt(0x1000); // 12비트

        // 62비트 랜덤 데이터
        long randomB = RANDOM.nextLong() & 0x3FFF_FFFF_FFFF_FFFFL; // 62비트

        // UUIDv7 비트 레이아웃 구성
        // timestamp_ms (48비트) | ver (4비트) | rand_a (12비트) | var (2비트) | rand_b (62비트)
        long mostSigBits = (timestampMs << 16) | (0x7L << 12) | randomA;
        long leastSigBits = (0x2L << 62) | randomB;

        UUID uuid = new UUID(mostSigBits, leastSigBits);
        return uuid.toString();
    }

    /**
     * UUIDv7을 UUID 객체로 생성
     *
     * @return UUID 객체
     */
    public static UUID generateUUID() {
        return UUID.fromString(generate());
    }
}
