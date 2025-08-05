package com.mypresentpast.backend.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommonFunctions {

    private CommonFunctions() {
    }

    public static String toSnakeCase(String input) {
        if (input == null) return null;
        return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    /**
     * Formatea una cadena capitalizando la primera letra de cada palabra
     * y pasando el resto a minúsculas. Útil para nombres, apellidos, ubicaciones, etc.
     */
    public static String formatAsTitleCase(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        return Arrays.stream(input.trim().toLowerCase().split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

}
