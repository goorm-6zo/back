package goorm.back.zo6.common.log;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Throwable;
}
