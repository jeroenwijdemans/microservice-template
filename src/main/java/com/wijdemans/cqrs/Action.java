package com.wijdemans.cqrs;

import com.wijdemans.ValueType;

public class Action extends ValueType<String> {

    public Action(String value) {
        super(value);
    }
}
