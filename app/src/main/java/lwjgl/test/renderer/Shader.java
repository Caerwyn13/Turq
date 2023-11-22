package lwjgl.test.renderer;

import java.io.IOException;
import java.nio.file.*;

import org.lwjgl.opengl.GL20;

public class Shader {
    private int shaderProgramID;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;

        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find first pattern after '#type'
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find second pattern after '#type'
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected Token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected Token '" + secondPattern + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not load shader file: " + filepath;
        }

        System.out.println(vertexSource);
        System.out.println(fragmentSource);
    }

    public void compile() {
        int vertexID, fragmentID;


        // ===============================================================
        // Compile and link shaders
        // ===============================================================
        
        // First load and compile vertex shader
        vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        // Pass source to GPU
        GL20.glShaderSource(vertexID, vertexSource);
        GL20.glCompileShader(vertexID);

        // Check for errors
        int success = GL20.glGetShaderi(vertexID, GL20.GL_COMPILE);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(vertexID, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(GL20.glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }


        // First load and compile fragment shader
        fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        // Pass source to GPU
        GL20.glShaderSource(fragmentID, fragmentSource);
        GL20.glCompileShader(fragmentID);

        // Check for errors
        success = GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetShaderi(fragmentID, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(GL20.glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders and check for errors
        shaderProgramID = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgramID, vertexID);
        GL20.glAttachShader(shaderProgramID, fragmentID);
        GL20.glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = GL20.glGetProgrami(shaderProgramID, GL20.GL_LINK_STATUS);
        if (success == GL20.GL_FALSE) {
            int len = GL20.glGetProgrami(shaderProgramID, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tShader linking failed.");
            System.out.println(GL20.glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        // Bind shader
        GL20.glUseProgram(shaderProgramID);
    }

    public void detatch() {
        GL20.glUseProgram(0);
    }
}
