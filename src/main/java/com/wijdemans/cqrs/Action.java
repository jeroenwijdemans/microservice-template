package com.wijdemans.cqrs;

public class Action {

    private String name;

    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
