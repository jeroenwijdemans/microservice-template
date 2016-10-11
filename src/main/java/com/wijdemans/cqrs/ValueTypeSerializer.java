package com.wijdemans.cqrs;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wijdemans.ValueType;

import java.io.IOException;

public class ValueTypeSerializer extends JsonSerializer<ValueType> {

    @Override
    public Class<ValueType> handledType() {
        return ValueType.class;
    }

    @Override
    public void serialize(ValueType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString("" + value.getValue());

    }
}
