package org.ever._4ever_be_alarm.common.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;
import com.fasterxml.uuid.StringArgGenerator;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * java-uuid-generator 라이브러리를 사용하여 다양한 버전의 UUID를 생성하는 유틸리티 클래스.
 * 각 생성기는 스레드 안전하며, 싱글톤처럼 사용하기 위해 지연 초기화됩니다.
 */
public final class UuidGenerator {

    // 동기화를 위한 Lock 객체들
    private static final Lock v1Lock = new ReentrantLock();
    private static final Lock v4Lock = new ReentrantLock();
    private static final Lock v5Lock = new ReentrantLock();
    private static final Lock v6Lock = new ReentrantLock();
    private static final Lock v7Lock = new ReentrantLock();
    private static final Lock v7RandomLock = new ReentrantLock();
    // 랜덤 생성기 (V4, V7 등에서 사용)
    // SecureRandom은 초기화 비용이 있으므로 미리 생성하거나 지연 로딩 권장
    private static final SecureRandom secureRandom = new SecureRandom();
    // 시간 기반 생성기용 MAC 주소 (없으면 자동으로 랜덤 멀티캐스트 주소 생성)
    // 애플리케이션 환경에 맞게 특정 인터페이스 주소를 가져오도록 수정할 수 있습니다.
    private static final EthernetAddress ethernetAddress = EthernetAddress.fromPreferredInterface(); //
    // --- 싱글톤 인스턴스 홀더 ---
    private static volatile NoArgGenerator timeBasedGenerator;
    private static volatile NoArgGenerator randomBasedGenerator;
    private static volatile StringArgGenerator nameBasedGeneratorSha1; // 기본 (V5)
    private static volatile NoArgGenerator timeBasedReorderedGenerator; // V6
    private static volatile NoArgGenerator timeBasedEpochGenerator;     // V7
    private static volatile NoArgGenerator timeBasedEpochRandomGenerator; // V7 (Random)

    private UuidGenerator() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    /**
     * 버전 1 (시간 기반) UUID를 생성합니다.
     * MAC 주소 정보를 사용하며, 동기화된 타이머를 사용합니다.
     *
     * @return 생성된 UUID (버전 1)
     */
    public static UUID generateV1() {
        if (timeBasedGenerator == null) {
            v1Lock.lock();
            try {
                if (timeBasedGenerator == null) {
                    // Generators.timeBasedGenerator()는 내부적으로 공유 타이머 사용
                    timeBasedGenerator = Generators.timeBasedGenerator(ethernetAddress); //
                }
            } finally {
                v1Lock.unlock();
            }
        }
        return timeBasedGenerator.generate();
    }

    /**
     * 버전 4 (랜덤 기반) UUID를 생성합니다.
     * SecureRandom을 사용하여 생성합니다.
     *
     * @return 생성된 UUID (버전 4)
     */
    public static UUID generateV4() {
        if (randomBasedGenerator == null) {
            v4Lock.lock();
            try {
                if (randomBasedGenerator == null) {
                    randomBasedGenerator = Generators.randomBasedGenerator(secureRandom); //
                }
            } finally {
                v4Lock.unlock();
            }
        }
        return randomBasedGenerator.generate();
    }

    /**
     * 버전 5 (이름 기반, SHA-1) UUID를 생성합니다.
     * 지정된 이름과 기본 URL 네임스페이스를 사용합니다.
     *
     * @param name UUID 생성을 위한 이름 문자열
     * @return 생성된 UUID (버전 5)
     */
    public static UUID generateV5(String name) {
        return generateV5(NameBasedGenerator.NAMESPACE_URL, name); //
    }

    /**
     * 버전 5 (이름 기반, SHA-1) UUID를 생성합니다.
     * 지정된 네임스페이스와 이름을 사용합니다.
     *
     * @param namespace UUID 네임스페이스
     * @param name      UUID 생성을 위한 이름 문자열
     * @return 생성된 UUID (버전 5)
     */
    public static UUID generateV5(UUID namespace, String name) {
        if (nameBasedGeneratorSha1 == null) {
            v5Lock.lock();
            try {
                if (nameBasedGeneratorSha1 == null) {
                    // 네임스페이스 없이 생성하면 generate 호출 시 네임스페이스를 지정해야 함
                    // 여기서는 편의상 기본 생성기를 만듦 (필요 시 특정 네임스페이스로 초기화 가능)
                    nameBasedGeneratorSha1 = Generators.nameBasedGenerator(null); // SHA-1 기본
                }
            } finally {
                v5Lock.unlock();
            }
        }
        // 이름 기반 생성기는 generate 호출 시 네임스페이스 바이트와 이름 바이트를 내부적으로 조합함
        // 만약 생성기 자체에 네임스페이스를 고정했다면 이름만 넘겨도 됨
        // 아래 코드는 namespace 인자를 존중 (null 이면 네임스페이스 없이 해싱)
        NameBasedGenerator specificGenerator = (namespace == null)
            ? (NameBasedGenerator) nameBasedGeneratorSha1
            : Generators.nameBasedGenerator(namespace); // 필요 시 매번 새 인스턴스 생성 (혹은 캐싱)

        return specificGenerator.generate(name);
    }

    /**
     * 버전 6 (시간 기반, 재정렬) UUID를 생성합니다.
     * 데이터베이스 인덱싱 성능에 유리합니다.
     *
     * @return 생성된 UUID (버전 6)
     */
    public static UUID generateV6() {
        if (timeBasedReorderedGenerator == null) {
            v6Lock.lock();
            try {
                if (timeBasedReorderedGenerator == null) {
                    timeBasedReorderedGenerator = Generators.timeBasedReorderedGenerator(
                        ethernetAddress); //
                }
            } finally {
                v6Lock.unlock();
            }
        }
        return timeBasedReorderedGenerator.generate();
    }

    /**
     * 버전 7 (Unix 시간 기반) UUID를 생성합니다.
     * 정렬 가능하며, 랜덤 값을 포함합니다.
     * 동일 밀리초 내 호출 시 비슷한 값이 나올 수 있습니다.
     *
     * @return 생성된 UUID (버전 7)
     */
    public static UUID generateV7() {
        if (timeBasedEpochGenerator == null) {
            v7Lock.lock();
            try {
                if (timeBasedEpochGenerator == null) {
                    timeBasedEpochGenerator = Generators.timeBasedEpochGenerator(secureRandom); //
                }
            } finally {
                v7Lock.unlock();
            }
        }
        return timeBasedEpochGenerator.generate();
    }

    /**
     * 버전 7 (Unix 시간 기반, 매번 랜덤) UUID를 생성합니다.
     * 정렬 가능하며, 매 호출마다 랜덤 파트를 새로 생성하여 동일 밀리초 내 충돌 가능성을 더욱 줄입니다.
     *
     * @return 생성된 UUID (버전 7, Random)
     */
    public static UUID generateV7Random() {
        if (timeBasedEpochRandomGenerator == null) {
            v7RandomLock.lock();
            try {
                if (timeBasedEpochRandomGenerator == null) {
                    timeBasedEpochRandomGenerator = Generators.timeBasedEpochRandomGenerator(
                        secureRandom); //
                }
            } finally {
                v7RandomLock.unlock();
            }
        }
        return timeBasedEpochRandomGenerator.generate();
    }

    // --- 테스트용 메인 메소드 ---
    public static void main(String[] args) {
        System.out.println("UUID V1: " + UuidGenerator.generateV1());
        System.out.println("UUID V4: " + UuidGenerator.generateV4());
        System.out.println("UUID V5 (URL, 'test'): " + UuidGenerator.generateV5("test"));
        System.out.println("UUID V5 (DNS, 'example.com'): " + UuidGenerator.generateV5(
            NameBasedGenerator.NAMESPACE_DNS, "example.com"));
        System.out.println("UUID V6: " + UuidGenerator.generateV6());
        System.out.println("UUID V7: " + UuidGenerator.generateV7());
        System.out.println("UUID V7 (Random): " + UuidGenerator.generateV7Random());
    }
}
