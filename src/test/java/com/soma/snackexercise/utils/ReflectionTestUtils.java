package com.soma.snackexercise.utils;

import com.soma.snackexercise.domain.BaseTimeEntity;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class ReflectionTestUtils {
    public static void setCreatedAt(BaseTimeEntity entity, LocalDateTime createdAt) throws Exception {
        Field field = BaseTimeEntity.class.getDeclaredField("createdAt");
        field.setAccessible(true);
        field.set(entity, createdAt);
    }

    public static void setUpdatedAt(BaseTimeEntity entity, LocalDateTime updatedAt) throws Exception {
        Field field = BaseTimeEntity.class.getDeclaredField("updatedAt");
        field.setAccessible(true);
        field.set(entity, updatedAt);
    }
}
