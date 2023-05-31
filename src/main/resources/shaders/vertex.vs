#version 400 core

in vec3 position;
in vec2 textureCoordinates;

out vec2 fragmentTextureCoordinates;

uniform mat4 transformationMatrix;

void main() {
    gl_Position = transformationMatrix * vec4(position, 1.0);
    fragmentTextureCoordinates = textureCoordinates;
}