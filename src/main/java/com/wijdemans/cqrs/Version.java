package com.wijdemans.cqrs;

import com.wijdemans.ValueType;

public class Version extends ValueType<String> {

    public Version(String value) {
        super(value);
    }
}
