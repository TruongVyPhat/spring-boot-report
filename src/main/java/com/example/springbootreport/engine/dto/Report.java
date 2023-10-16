package com.example.springbootreport.engine.dto;

import java.util.List;

public class Report {
    private String title;
    private String name;
    private List<Parameter> parameters;

    public Report(String title, String name) {
        this.title = title;
        this.name = name;
    }

    public static class Parameter {
        private String title;
        private String name;
        private ParameterType type;

    }

    public enum ParameterType {
        INT, STRING
    }
}
