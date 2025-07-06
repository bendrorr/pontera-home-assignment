package com.example.pontera.home.assignment.util;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {

    public static Set<Integer> extractFieldAsIntSet(Response response, String fieldName) {
        return response.jsonPath().getList("$", JsonNode.class)
                .stream()
                .filter(Objects::nonNull)
                .map(jsonNode -> jsonNode.get(fieldName).asInt())
                .collect(Collectors.toSet());
    }

}
