package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

/** GET /billers cavabı — xidmət təchizatçıları kataloqu (schemas/billers-list.json). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillerList {

    private int total;
    private List<Biller> items;

    public int getTotal() {
        return total;
    }

    public List<Biller> getItems() {
        return items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Biller {

        private String id;
        private String name;
        private String category;
        private String subscriberFormat;
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
        private Fee fee;
        private String subscriberPattern;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public String getSubscriberFormat() {
            return subscriberFormat;
        }

        public BigDecimal getMinAmount() {
            return minAmount;
        }

        public BigDecimal getMaxAmount() {
            return maxAmount;
        }

        public Fee getFee() {
            return fee;
        }

        public String getSubscriberPattern() {
            return subscriberPattern;
        }
    }

    /** fee.type: NONE | PERCENT (value, min, max) | FIXED (value). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fee {

        private String type;
        private BigDecimal value;
        private BigDecimal min;
        private BigDecimal max;

        public String getType() {
            return type;
        }

        public BigDecimal getValue() {
            return value;
        }

        public BigDecimal getMin() {
            return min;
        }

        public BigDecimal getMax() {
            return max;
        }
    }
}
