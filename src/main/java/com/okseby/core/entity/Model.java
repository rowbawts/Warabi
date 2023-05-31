package com.okseby.core.entity;

import lombok.Getter;
import lombok.Setter;

public class Model {
    @Getter private int id;
    @Getter private int vertexCount;

    @Getter @Setter private Texture texture;

    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
    }

    public Model(int id, int vertexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.texture = texture;
    }

    public Model(Model model, Texture texture) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.texture = texture;
    }
}
