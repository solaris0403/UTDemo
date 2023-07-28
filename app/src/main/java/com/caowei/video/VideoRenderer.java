package com.caowei.video;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoRenderer implements GLTextureView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static String TAG = "VideoRenderer";

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 4 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 2;

    //左右分布（包含左色彩右黑白、左黑白右色彩）的顶点坐标和纹理坐标，每四个中，前两位为顶点坐标，后两位为纹理坐标
    private static final float[] triangleVerticesData = {
            -1.0f, 1.0f, 0.5f, 0.0f,
            1.0f, 1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.5f, 1.0f,
            1.0f, -1.0f, 1.0f, 1.0f,
    };

    private FloatBuffer triangleVertices;

    private static final String vertexShader = "attribute vec2 a_position;\n"
            + "attribute vec2 a_texCoord;\n"
            + "varying vec2 v_texcoord;\n"
            + "void main(void) {\n"
            + "  gl_Position = vec4(a_position, 0.0, 1.0);\n"
            + "  v_texcoord = a_texCoord;\n"
            + "}\n";

    /**
     * 左彩色、右黑白
     */
    private static final String alphaShader = "#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "varying vec2 v_texcoord;\n"
            + "uniform samplerExternalOES sTexture;\n"
            + "void main() {\n"
            + "  gl_FragColor = vec4(texture2D(sTexture, v_texcoord + vec2(-0.5, 0)).rgb, texture2D(sTexture, v_texcoord).r);\n"
            + "}\n";

    private int program;
    private int textureID;
    private int aPositionHandle;
    private int aTextureHandle;

    private SurfaceTexture surface;
    private boolean updateSurface = false;

    private OnSurfacePrepareListener onSurfacePrepareListener;

    public VideoRenderer() {
        triangleVertices = ByteBuffer.allocateDirect(triangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        triangleVertices.put(triangleVerticesData).position(0);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        synchronized (this) {
            if (updateSurface) {
                //将外部图像数据拷贝到OpenGL ES纹理中的一个缓冲区，
                // 并在下一次OpenGL渲染时使用该缓冲区中的数据进行纹理采样和渲染。
                surface.updateTexImage();
                updateSurface = false;
            }
        }
        //清除OpenGL的帧缓冲区，深度缓冲区和颜色缓冲区
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        //启用混合
        GLES20.glEnable(GLES20.GL_BLEND);

        //第一个参数sfactor表示源颜色混合因子，第二个参数dfactor表示目标颜色混合因子。
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //设置清除颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //指定当前使用的着色器
        GLES20.glUseProgram(program);
        checkGlError("glUseProgram");

        //指定当前活动的纹理单元为纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //将textureID指定的纹理对象绑定到纹理单元0上。
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);

        //移动指针位置
        triangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);

        //将 triangleVertices 中的顶点位置数据绑定到着色器中的 aPositionHandle 属性，并指定顶点数据的格式。
        GLES20.glVertexAttribPointer(aPositionHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);
        checkGlError("glVertexAttribPointer maPosition");

        //启用指定索引位置的顶点属性数组。
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        checkGlError("glEnableVertexAttribArray aPositionHandle");

        //顶点数据数组中纹理坐标数据的偏移量
        triangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        //为顶点着色器中的顶点属性指定数据源。
        GLES20.glVertexAttribPointer(aTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);
        checkGlError("glVertexAttribPointer aTextureHandle");

        //启用顶点着色器中指定属性的数据传递。
        GLES20.glEnableVertexAttribArray(aTextureHandle);
        checkGlError("glEnableVertexAttribArray aTextureHandle");

        //绘制图元
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");

        //它会阻塞CPU线程，直到GPU执行完所有之前提交的OpenGL指令
//        GLES20.glFinish();
    }

    @Override
    public void onSurfaceDestroyed(GL10 gl) {
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        logI("onSufaceChanged w " + width + " h " + height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        //创建着色器程序
        program = createProgram(vertexShader, alphaShader);
        if (program == 0) {
            return;
        }
        //获取顶点着色器中属性变量"a_position"的位置句柄，用于后续绑定数据。
        aPositionHandle = GLES20.glGetAttribLocation(program, "a_position");
        checkGlError("glGetAttribLocation aPosition");
        if (aPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        //获取顶点着色器中属性变量"a_texCoord"的位置句柄，用于后续绑定纹理坐标数据。
        aTextureHandle = GLES20.glGetAttribLocation(program, "a_texCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (aTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        prepareSurface();
    }

    //主要是为绘制做好准备，包括创建纹理、设置纹理参数、创建SurfaceTexture等。
    private void prepareSurface() {
        int[] textures = new int[1];
        //使用OpenGL ES生成一个纹理对象的句柄，并将句柄保存在textures数组中。
        GLES20.glGenTextures(1, textures, 0);
        //获取生成的纹理句柄，将其保存在textureID变量中。
        textureID = textures[0];
        //将textureID绑定到这个纹理目标上。
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);
        checkGlError("glBindTexture textureID");
        //设置纹理的过滤参数，GL_NEAREST 表示使用最近邻过滤，即像素颜色由最近的纹理像素确定。
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        //设置纹理的过滤参数, GL_LINEAR 表示使用线性过滤，即像素颜色由附近的纹理像素进行线性插值得到。
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        //创建一个SurfaceTexture对象，用于接收外部纹理数据，并绑定到之前创建的纹理句柄textureID上。
        surface = new SurfaceTexture(textureID);
        //设置SurfaceTexture的帧可用监听器
        surface.setOnFrameAvailableListener(this);
        //创建一个新的Surface对象，并将前面创建的SurfaceTexture对象this.surface作为参数传入。
        //这里创建的Surface对象用于将视频数据渲染到屏幕上。
        Surface surface = new Surface(this.surface);
        if (onSurfacePrepareListener != null) {
            // 将上面创建的Surface对象传递给onSurfacePrepareListener，
            // 这个监听器会处理Surface的准备工作，用于后续的视频渲染。
            onSurfacePrepareListener.surfacePrepared(surface);
        }
        synchronized (this) {
            //表示目前没有新的帧数据可用，等待下一次渲染。
            updateSurface = false;
        }
    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
    }

    //加载和编译着色器
    private int loadShader(int shaderType, String source) {
        //创建一个着色器
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            //将着色器源代码传递给着色器对象
            GLES20.glShaderSource(shader, source);
            //编译着色器源代码，将其转换为GPU可执行的格式。
            GLES20.glCompileShader(shader);
            //创建一个整型数组compiled用于存储编译状态。
            int[] compiled = new int[1];
            //获取着色器的编译状态，并将结果存储在compiled数组中。
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                //如果编译失败
                logI("Could not compile shader " + shaderType
                        + ":\n" + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    //创建顶点着色器和片段着色器对象，并将它们链接成一个完整的着色器程序
    private int createProgram(String vertexSource, String fragmentSource) {
        //顶点着色器对象
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        //片段着色器对象
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        //创建一个着色器程序对象，并返回它的整数句柄。
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            //顶点着色器附加到着色器程序上。
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            //将片段着色器附加到着色器程序上。
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            //链接着色器程序，将顶点着色器和片段着色器组合成一个完整的着色器程序。
            GLES20.glLinkProgram(program);
            //创建一个整型数组linkStatus用于存储链接状态。
            int[] linkStatus = new int[1];
            //获取着色器程序的链接状态，并将结果存储在linkStatus数组中。
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                logI("Could not link program: " + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 检查OpenGL操作是否产生错误。
     *
     * @param op 操作
     */
    private void checkGlError(String op) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            postError(op + ": checkGlError glError " + error);
        }
    }

    void setOnSurfacePrepareListener(OnSurfacePrepareListener onSurfacePrepareListener) {
        this.onSurfacePrepareListener = onSurfacePrepareListener;
    }

    private int mCurrentVideoWidth;
    private int mCurrentVideoHeight;
    private int mCurrentViewWidth;
    private int mCurrentViewHeight;

    public void updateRadio(int videoWidth, int videoHeight, int viewWidth, int viewHeight) {
        if (mCurrentVideoWidth == videoWidth
                && mCurrentVideoHeight == videoHeight
                && mCurrentViewWidth == viewWidth
                && mCurrentViewHeight == viewHeight
        ) {
            return;
        }
        mCurrentVideoWidth = videoWidth;
        mCurrentVideoHeight = videoHeight;

        mCurrentViewWidth = viewWidth;
        mCurrentViewHeight = viewHeight;

        if (triangleVertices == null) {
            triangleVertices = ByteBuffer.allocateDirect(
                            triangleVerticesData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        triangleVertices.clear();
        triangleVertices.put(triangleVerticesData).position(0);
        float videoRadio = videoWidth * 1.0f / videoHeight;
        float viewRadio = viewWidth * 1.0f / viewHeight;
        if (videoRadio > viewRadio) {
            float offset = videoWidth - (1.0f * viewWidth * videoHeight) / viewHeight;
            float scale = offset / videoWidth;
            FloatBuffer fb = triangleVertices;

            //  s1
            int startIndex = 2;
            float s1 = fb.get(startIndex);
            s1 = s1 + scale / 2;
            fb.put(startIndex, s1);

            //  s2
            startIndex = startIndex + 4;
            float s2 = fb.get(startIndex);
            s2 = s2 - scale / 2;
            fb.put(startIndex, s2);

            //  s3
            startIndex = startIndex + 4;
            float s3 = fb.get(startIndex);
            s3 = s3 + scale / 2;
            fb.put(startIndex, s3);

            //  s4
            startIndex = startIndex + 4;
            float s4 = fb.get(startIndex);
            s4 = s4 - scale / 2;
            fb.put(startIndex, s4);
        } else {
            float offset = videoHeight - (1.0f * viewHeight * videoWidth) / viewWidth;
            float scale = offset / videoHeight;
            FloatBuffer fb = triangleVertices;

            //  s1
            int startIndex = 3;
            float s1 = fb.get(startIndex);
            s1 = s1 + scale / 2;
            fb.put(startIndex, s1);

            //  s2
            startIndex = startIndex + 4;
            float s2 = fb.get(startIndex);
            s2 = s2 + scale / 2;
            fb.put(startIndex, s2);

            //  s3
            startIndex = startIndex + 4;
            float s3 = fb.get(startIndex);
            s3 = s3 - scale / 2;
            fb.put(startIndex, s3);

            //  s4
            startIndex = startIndex + 4;
            float s4 = fb.get(startIndex);
            s4 = s4 - scale / 2;
            fb.put(startIndex, s4);
        }
    }

    interface OnSurfacePrepareListener {
        void surfacePrepared(Surface surface);
    }

    private void logI(String log) {
        Log.i(TAG, log);
    }

    private void logE(String log) {
        Log.e(TAG, log);
    }

    private void postError(String error) {
        Log.e(TAG, error);
    }
}