package com.okseby.core;

import com.okseby.core.entity.Model;
import com.okseby.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadOBJModel(String fileName) {
        List<String> lines = Utils.readAllLines(fileName);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    // vertices
                    Vector3f verticesVector = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVector);
                    break;
                case "vn":
                    // vertex normals
                    Vector3f normalsVector = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normalsVector);
                    break;
                case "vt":
                    // vertex textures
                    Vector2f texturesVector = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(texturesVector);
                    break;
                case "f":
                    // vertex faces
                    for (int i = 1; i < tokens.length; i++) {
                        processFaces(tokens[i], faces);
                    }
                    break;
                default:
                    break;

            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = new float[vertices.size() * 3];

        int i = 0;
        for (Vector3f position : vertices) {
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            i++;
        }

        float[] textureCoordinatesArray = new float[vertices.size() * 2];
        float[] normalArray = new float[vertices.size() * 3];

        for (Vector3i face : faces) {
            processVertexes(face.x, face.y, face.z, textures, normals, indices, textureCoordinatesArray, normalArray);
        }

        int[] indicesArray = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArray, textureCoordinatesArray, indicesArray);
    }

    private static void processVertexes(int position, int textureCoordinate, int normal, List<Vector2f> textureCoordinateList, List<Vector3f> normalList, List<Integer> indicesList, float[] textureCoordinatesArray, float[] normalArray) {
        indicesList.add(position);

        if (textureCoordinate >= 0) {
            Vector2f textureCoordinatesVector = textureCoordinateList.get(textureCoordinate);

            textureCoordinatesArray[position * 2] = textureCoordinatesVector.x;
            textureCoordinatesArray[position * 2 + 1] = 1 - textureCoordinatesVector.y;
        }

        if (normal >= 0) {
            Vector3f normalVector = normalList.get(normal);

            normalArray[position * 3] = normalVector.x;
            normalArray[position * 3 + 1] = normalVector.y;
            normalArray[position * 3 + 2] = normalVector.z;
        }
    }

    private static void processFaces(String token, List<Vector3i> faces) {
        String[] lineToken = token.split("/");

        int length = lineToken.length;
        int position = -1, coordinates = -1, normal = -1;

        position = Integer.parseInt(lineToken[0]) - 1;

        if (length > 1) {
            String textureCoordinates = lineToken[1];
            coordinates = textureCoordinates.length() > 0 ? Integer.parseInt(textureCoordinates) - 1 : -1;

            if (length > 2)
                normal = Integer.parseInt(lineToken[2]) - 1;
        }

        Vector3i facesVector = new Vector3i(position, coordinates, normal);

        faces.add(facesVector);
    }

    public Model loadModel(float[] vertices, float[] textureCoordinates, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        storeDataInAttributeList(1, 2, textureCoordinates);
        unbind();

        return new Model(id, indices.length);
    }

    public int loadTexture(String fileName) throws Exception {
        int width, height;
        ByteBuffer buffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(ObjectLoader.class.getResource(fileName).getFile(), w, h, c, 4);
            if (buffer == null)
                throw new Exception("Image File: '" + fileName + "' not loaded, error: " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();

        textures.add(id);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);

        return id;
    }

    private int createVAO() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);

        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();

        vbos.add(vbo);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);

        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();

        vbos.add(vbo);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        for (int vao : vaos)
            GL30.glDeleteVertexArrays(vao);
        for (int vbo : vbos)
            GL30.glDeleteBuffers(vbo);
        for (int texture : textures)
            GL11.glDeleteTextures(texture);
    }
}
