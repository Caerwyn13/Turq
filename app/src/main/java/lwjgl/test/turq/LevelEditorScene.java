package lwjgl.test.turq;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import lwjgl.test.renderer.Shader;

public class LevelEditorScene extends Scene {
    // ? May not be needed
    /* private String vertexShaderSource = "#version 330 core\r\n" + //
            "layout (location = 0) in vec3 aPos;\r\n" + //
            "layout (location = 1) in vec4 aColour;\r\n" + //
            "\r\n" + //
            "out vec4 fColour;\r\n" + //
            "\r\n" + //
            "void main()\r\n" + //
            "{\r\n" + //
            "    fColour = aColour;\r\n" + //
            "    gl_Position = vec4(aPos, 1.0);\r\n" + //
            "}";
    private String fragmentShaderSource = "#version 330 core\r\n" + //
            "\r\n" + //
            "in vec4 fColour;\r\n" + //
            "\r\n" + //
            "out vec4 color;\r\n" + //
            "\r\n" + //
            "void main()\r\n" + //
            "{\r\n" + //
            "    color = fColour;\r\n" + //
            "}";

    private int vertexID, fragmentID, shaderProgram; */
    private float[] vertexArray = {
        // Position             // Colour
        0.5f , -0.5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,      // Bottom Right 0
        -0.5f, 0.5f , 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,      // Top Left     1
        0.5f , 0.5f , 0.0f,     0.0f, 0.0f, 1.0f, 1.0f,      // Top Right    2
        -0.5f, -0.5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f,      // Bottom Left  3
    };

    // ! IMPORTANT: MUST BE IN COUNTER-CLOCKWISE ORDER
    private int[] elementArray = {
        2, 1, 0, // Top Right Triangle
        0, 1, 3, // Bottom Left Triangle
    };

    private int vaoID, vboID, eboID;
    private Shader defaultShader;

    public LevelEditorScene() {
        System.out.println("Inside Level Editor Scene");
    }

    @Override
    public void init() {
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        // ===============================================================
        // Generate VAO, BVO, and EBO buffer objects, and send to GPU
        // ===============================================================
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        // Create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload vertex buffer
        vboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuffer, GL30.GL_STATIC_DRAW);

        // Create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL30.GL_STATIC_DRAW);

        // Add vertex attribute pointers
        int positionSize = 3;
        int colourSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colourSize) * floatSizeBytes;
        GL20.glVertexAttribPointer(0, positionSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, colourSize, GL20.GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        GL20.glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        // Bind VAO
        GL30.glBindVertexArray(vaoID);
        
        // Enable vertex attributes
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL20.glDrawElements(GL20.GL_TRIANGLES, elementArray.length, GL20.GL_UNSIGNED_INT, 0);

        // Unbind everything
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
        defaultShader.detatch();
    }
}
