package rehab;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 只验证环境：JDK 21 + JUnit 5 能跑起来。实现写完后可以删掉这个文件。 */
class ToolchainSmokeTest {

    @Test
    void jdkIsAtLeast21() {
        int feature = Runtime.version().feature();
        assertTrue(feature >= 21, "当前 JDK 是 " + feature);
        System.out.println("JVM: " + System.getProperty("java.version"));
    }

    @Test
    void junitWorks() {
        assertEquals(2, 1 + 1);
    }
}
