package com.util;

@FunctionalInterface
public interface SixConsumer<T1, T2, T3, T4, T5, T6> {
    void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
}

