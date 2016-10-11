package com.wijdemans.cqrs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wijdemans.ValueType;

import java.io.IOException;

public class ValueTypeDeserializer extends JsonDeserializer<ValueType<String>> {

    // TODO scan classes on ValueObject and register them here

    @Override
    public ValueType<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        if (!p.hasToken(JsonToken.VALUE_STRING)) {
            throw new UnsupportedOperationException(String.format("Expected a string value, got [%s] in [%s]", p.getCurrentToken(), p.getCurrentName()));
        }
        // TODO instead of hard coded values use the registered classes
        switch (p.getCurrentName()) {
            case "action":
                return new Action(p.getText());
            case "version":
                return new Version(p.getText());
            default:
                throw new UnsupportedOperationException(String.format("Cannot determine what to do with [%s] and value [%s]", p.getCurrentName(), p.getText()));
        }

    }
}
