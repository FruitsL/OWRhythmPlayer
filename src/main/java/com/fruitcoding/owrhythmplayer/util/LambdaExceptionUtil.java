package com.fruitcoding.owrhythmplayer.util;

import java.util.function.Function;
import java.util.function.Predicate;

public class LambdaExceptionUtil {
    /**
     * 람다식 filter에서 사용하는 exception 처리가 필요없는 Predicate 메서드
     *
     * @param predicate
     * @return
     * @param <T>
     */
    public static <T> Predicate<T> rethrowPredicate(PredicateWithException<T> predicate) {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    public interface PredicateWithException<T> {
        boolean test(T t) throws Exception;
    }

    /**
     * 람다식 map에서 사용하는 exception 처리가 필요없는 Function 메서드
     *
     * @param function
     * @return
     * @param <T>
     * @param <R>
     */
    public <T, R> Function<T, R> rethrowFunction(FunctionWithException<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    public interface FunctionWithException<T, R> {
        R apply(T t) throws Exception;
    }
}
