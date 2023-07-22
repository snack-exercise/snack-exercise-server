package com.soma.snackexercise.dto.signup;

import com.soma.snackexercise.util.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignupRequest {
    private String name;
    private Gender gender;
    private Integer birthYear;
}