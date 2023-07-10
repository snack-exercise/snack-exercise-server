package com.soma.snackexercise.util.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Success<T> implements Result {
    private T data;
}
