package com.ares.core.thread.task;

public interface EventFunction<T> {
    void apply(long p1, T p2);
}
