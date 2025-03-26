package goorm.back.zo6.common.log;

@FunctionalInterface
public interface ThrowableCallable<V> {
    V call() throws Throwable;
}
