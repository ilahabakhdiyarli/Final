package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Biznes xətası cavabı: { "error": "...", "code": "...", "details"?: [ { field, message } ] }
 * (schemas/error.json, schemas/validation-error.json).
 * Bəzi kodlar əlavə sahə daşıyır (məs. LIMIT_REACHED → limit, DAILY_LIMIT_EXCEEDED → remaining) —
 * onlar {@link #get(String)} ilə oxunur.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

    private String error;
    private String code;
    private List<Detail> details = new ArrayList<>();
    private final Map<String, Object> extra = new HashMap<>();

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public List<Detail> getDetails() {
        return details;
    }

    /** details[].field siyahısı — hansı sahələrin səhv olduğunu yoxlamaq üçün. */
    public List<String> getDetailFields() {
        return details.stream().map(Detail::getField).collect(Collectors.toList());
    }

    @JsonAnySetter
    void putExtra(String key, Object value) {
        extra.put(key, value);
    }

    public Object get(String key) {
        return extra.get(key);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detail {

        private String field;
        private String message;

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}
