package io.github.keibai.models.meta;

import io.github.keibai.models.Model;

public class Error implements Model {
    public String error;

    @Override
    public String toString() {
        return error;
    }
}
