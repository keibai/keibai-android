package io.github.keibai.models.meta;

import io.github.keibai.models.Model;

public class Msg implements Model {
    public String msg;

    @Override
    public String toString() {
        return msg;
    }
}
