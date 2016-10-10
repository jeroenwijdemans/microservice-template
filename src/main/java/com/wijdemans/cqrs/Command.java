package com.wijdemans.cqrs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Command {

    // TODO make value objects out of these
    private String version;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime userDate;
    private String action;
    private JSONObject payload;

    public Command() {
    }

    public Command(String version, LocalDateTime userDate, String action, JSONObject payload) {
        this.version = version;
        this.userDate = userDate;
        this.action = action;
        this.payload = payload;
    }

    public String getVersion() {
        return version;
    }

    public LocalDateTime getUserDate() {
        return userDate;
    }

    public String getAction() {
        return action;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setUserDate(LocalDateTime userDate) {
        this.userDate = userDate;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }
}
