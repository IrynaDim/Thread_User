package com.dev.thread.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = User.COLLECTION_NAME)
public class User implements Serializable {
    public static final String COLLECTION_NAME = "users";

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private Double sum;

    @Override
    public String toString() {
        return "User { id = " + id + ", name = " + name
                + ", sum = '" + sum + '}';
    }
}
