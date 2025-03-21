package goorm.back.zo6.test;

import org.junit.jupiter.api.Test;

public class TestProcessCore {

    @Test
    void testAvailableProcessors() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available Processors (Logical Cores): " + availableProcessors);
    }
}
