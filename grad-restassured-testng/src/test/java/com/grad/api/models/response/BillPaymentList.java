package com.grad.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

/** GET /bill-payments cavabı — səhifələnmiş ödəniş tarixçəsi (schemas/bill-payment-list.json). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillPaymentList {

    private int page;
    private int limit;
    private int total;
    private int totalPages;
    private List<Item> items;

    public int getPage() {
        return page;
    }

    public int getLimit() {
        return limit;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Item> getItems() {
        return items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        private String id;
        private String receiptNo;
        private String billerId;
        private String subscriberNo;
        private BigDecimal amount;
        private BigDecimal fee;
        private BigDecimal total;
        private String status;
        private String createdAt;

        public String getId() {
            return id;
        }

        public String getReceiptNo() {
            return receiptNo;
        }

        public String getBillerId() {
            return billerId;
        }

        public String getSubscriberNo() {
            return subscriberNo;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public BigDecimal getFee() {
            return fee;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public String getStatus() {
            return status;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
