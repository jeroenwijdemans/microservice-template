package com.wijdemans.cqrs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Command {

    @JsonDeserialize(using = ValueTypeDeserializer.class)
    @JsonSerialize(using = ValueTypeSerializer.class)
    private Version version;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime userDate;
    @JsonDeserialize(using = ValueTypeDeserializer.class)
    @JsonSerialize(using = ValueTypeSerializer.class)
    private Action action;
    private JSONObject payload;

    public Command() {
    }

    public Command(Version version, LocalDateTime userDate, Action action, JSONObject payload) {
        this.version = version;
        this.userDate = userDate;
        this.action = action;
        this.payload = payload;
    }

    public Version getVersion() {
        return version;
    }

    public LocalDateTime getUserDate() {
        return userDate;
    }

    public Action getAction() {
        return action;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setUserDate(LocalDateTime userDate) {
        this.userDate = userDate;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }
}
