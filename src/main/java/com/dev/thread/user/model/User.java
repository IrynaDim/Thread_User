package com.dev.thread.user.model;

import net.bytebuddy.implementation.bind.annotation.Empty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = User.COLLECTION_NAME)
public class User implements Serializable {
    public static final String COLLECTION_NAME = "users";

    @Id
    private long user_id;

    @Indexed(unique = true)
    private String user_name;
    private Double sum;

    public Long getId() {
        return user_id;
    }

    public void setId(Long user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return user_name;
    }

    public void setName(String user_name) {
        this.user_name = user_name;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "User { id = " + user_id + ", name = " + user_name
                + ", sum = '" + sum + '}';
    }
}
