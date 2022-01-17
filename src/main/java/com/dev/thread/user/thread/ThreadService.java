package com.dev.thread.user.thread;

import com.dev.thread.user.model.User;

import java.util.Map;

public interface ThreadService {
    Map<String, User> run();
}
