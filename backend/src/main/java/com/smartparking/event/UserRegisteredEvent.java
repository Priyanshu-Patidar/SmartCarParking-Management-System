package com.smartparking.event;

import com.smartparking.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserRegisteredEvent {
    private final User user;
}
