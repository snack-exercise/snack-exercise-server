package com.soma.snackexercise.factory.dto;

import com.soma.snackexercise.dto.exgroup.request.ExgroupCreateRequest;

import java.time.LocalTime;

public class ExgroupCreateFactory {
    public static ExgroupCreateRequest createExgroupCreateRequest() {
        return new ExgroupCreateRequest("name", "emozi", "color", "description", 3, 10,
                LocalTime.now(), LocalTime.now().plusHours(1), "커피 쏘기", 20, 2, 10);
    }
}
