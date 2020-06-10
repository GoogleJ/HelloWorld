//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities.videocompressor.videoslimmer.render;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TextureRenderer {
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 20;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private static final float[] mTriangleVerticesData = new float[]{-1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F};
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private FloatBuffer mTriangleVertices;
    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];
    private int mProgram;
    private int mTextureID = -1234567;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private int rotationAngle = 0;

    public TextureRenderer() {
        this.mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mTriangleVertices.put(mTriangleVerticesData).position(0);
        Matrix.setIdentityM(this.mSTMatrix, 0);
    }

    public int getTextureId() {
        return this.mTextureID;
    }

    public void drawFrame(SurfaceTexture st) {
        this.checkGlError("onDrawFrame start");
        st.getTransformMatrix(this.mSTMatrix);
        GLES20.glUseProgram(this.mProgram);
        this.checkGlError("glUseProgram");
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, this.mTextureID);
        this.mTriangleVertices.position(0);
        GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 20, this.mTriangleVertices);
        this.checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(this.maPositionHandle);
        this.checkGlError("glEnableVertexAttribArray maPositionHandle");
        this.mTriangleVertices.position(3);
        GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, 20, this.mTriangleVertices);
        this.checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(this.maTextureHandle);
        this.checkGlError("glEnableVertexAttribArray maTextureHandle");
        GLES20.glUniformMatrix4fv(this.muSTMatrixHandle, 1, false, this.mSTMatrix, 0);
        GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, this.mMVPMatrix, 0);
        GLES20.glDrawArrays(5, 0, 4);
        this.checkGlError("glDrawArrays");
        GLES20.glFinish();
    }

    public void surfaceCreated() {
        this.mProgram = this.createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
        if (this.mProgram == 0) {
            throw new RuntimeException("failed creating program");
        } else {
            this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
            this.checkGlError("glGetAttribLocation aPosition");
            if (this.maPositionHandle == -1) {
                throw new RuntimeException("Could not get attrib location for aPosition");
            } else {
                this.maTextureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
                this.checkGlError("glGetAttribLocation aTextureCoord");
                if (this.maTextureHandle == -1) {
                    throw new RuntimeException("Could not get attrib location for aTextureCoord");
                } else {
                    this.muMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");
                    this.checkGlError("glGetUniformLocation uMVPMatrix");
                    if (this.muMVPMatrixHandle == -1) {
                        throw new RuntimeException("Could not get attrib location for uMVPMatrix");
                    } else {
                        this.muSTMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uSTMatrix");
                        this.checkGlError("glGetUniformLocation uSTMatrix");
                        if (this.muSTMatrixHandle == -1) {
                            throw new RuntimeException("Could not get attrib location for uSTMatrix");
                        } else {
                            int[] textures = new int[1];
                            GLES20.glGenTextures(1, textures, 0);
                            this.mTextureID = textures[0];
                            GLES20.glBindTexture(36197, this.mTextureID);
                            this.checkGlError("glBindTexture mTextureID");
                            GLES20.glTexParameterf(36197, 10241, 9728.0F);
                            GLES20.glTexParameterf(36197, 10240, 9729.0F);
                            GLES20.glTexParameteri(36197, 10242, 33071);
                            GLES20.glTexParameteri(36197, 10243, 33071);
                            this.checkGlError("glTexParameter");
                            Matrix.setIdentityM(this.mMVPMatrix, 0);
                            if (this.rotationAngle != 0) {
                                Matrix.rotateM(this.mMVPMatrix, 0, (float) this.rotationAngle, 0.0F, 0.0F, 1.0F);
                            }

                        }
                    }
                }
            }
        }
    }

    public void changeFragmentShader(String fragmentShader) {
        GLES20.glDeleteProgram(this.mProgram);
        this.mProgram = this.createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", fragmentShader);
        if (this.mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        this.checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compiled, 0);
        if (compiled[0] == 0) {
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        return shader;
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = this.loadShader(35633, vertexSource);
        if (vertexShader == 0) {
            return 0;
        } else {
            int pixelShader = this.loadShader(35632, fragmentSource);
            if (pixelShader == 0) {
                return 0;
            } else {
                int program = GLES20.glCreateProgram();
                this.checkGlError("glCreateProgram");
                if (program == 0) {
                    return 0;
                } else {
                    GLES20.glAttachShader(program, vertexShader);
                    this.checkGlError("glAttachShader");
                    GLES20.glAttachShader(program, pixelShader);
                    this.checkGlError("glAttachShader");
                    GLES20.glLinkProgram(program);
                    int[] linkStatus = new int[1];
                    GLES20.glGetProgramiv(program, 35714, linkStatus, 0);
                    if (linkStatus[0] != 1) {
                        GLES20.glDeleteProgram(program);
                        program = 0;
                    }

                    return program;
                }
            }
        }
    }

    public void checkGlError(String op) {
        int error;
        if ((error = GLES20.glGetError()) != 0) {
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}