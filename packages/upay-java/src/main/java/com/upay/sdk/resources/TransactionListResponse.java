package com.upay.sdk.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Response DTO for transaction list operations
 */
public class TransactionListResponse {
    
    @JsonProperty("data")
    private List<JsonNode> data;
    
    @JsonProperty("pagination")
    private Pagination pagination;
    
    @JsonProperty("message")
    private String message;
    
    public List<JsonNode> getData() {
        return data;
    }
    
    public void setData(List<JsonNode> data) {
        this.data = data;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public static class Pagination {
        @JsonProperty("total")
        private Integer total;
        
        @JsonProperty("page")
        private Integer page;
        
        @JsonProperty("limit")
        private Integer limit;
        
        @JsonProperty("totalPages")
        private Integer totalPages;
        
        public Integer getTotal() {
            return total;
        }
        
        public void setTotal(Integer total) {
            this.total = total;
        }
        
        public Integer getPage() {
            return page;
        }
        
        public void setPage(Integer page) {
            this.page = page;
        }
        
        public Integer getLimit() {
            return limit;
        }
        
        public void setLimit(Integer limit) {
            this.limit = limit;
        }
        
        public Integer getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }
    }
}
