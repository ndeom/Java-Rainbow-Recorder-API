package com.rainbowrecorder.api.constants;

public enum ResponseTypes {
    ERROR ("error"),
    MESSAGE ("message");

    public final String label;

    private ResponseTypes(String label) {
        this.label = label;
    }
}
