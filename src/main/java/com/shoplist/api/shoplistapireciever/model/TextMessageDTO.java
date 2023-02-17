package com.shoplist.api.shoplistapireciever.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextMessageDTO {
    private String type;
    private Product payload;

    public enum Type {
        ADD("ADD"),
        DELETE("DELETE"),
        UPDATE("UPDATE"),
        ERROR("ERROR");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
