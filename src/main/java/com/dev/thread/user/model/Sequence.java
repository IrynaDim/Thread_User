package com.dev.thread.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Sequence.COLLECTION_NAME)
public class Sequence {
    public static final String COLLECTION_NAME = "sequences";

    @Id
    private String id;
    private Long value;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}

