package com.okseby.core.entity;

import lombok.Getter;

public class Model {
    @Getter private int id;
    @Getter private int vertexCount;

    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
    }
}
