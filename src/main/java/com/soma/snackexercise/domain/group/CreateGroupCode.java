package com.soma.snackexercise.domain.group;

import java.util.Random;

public class CreateGroupCode {

    private static Random random = new Random();
    private static int leftLimit = 48; // numeral '0'
    private static int rightLimit = 57; // numeral '9'
    private static int targetStringLength = 6;

    public static String createGroupCode(){
        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
