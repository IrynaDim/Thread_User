package com.dev.thread.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Data
@Document(collection = User.COLLECTION_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Service
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
