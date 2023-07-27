package com.soma.snackexercise.factory.dto;

import com.soma.snackexercise.dto.exgroup.request.ExgroupCreateRequest;

import java.time.LocalTime;

public class ExgroupCreateFactory {
    public static ExgroupCreateRequest createExgroupCreateRequest() {
        return new ExgroupCreateRequest("name", "emozi", "color", "description", 3, 10,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "커피 쏘기", 20, 2, 10);
    }
}
