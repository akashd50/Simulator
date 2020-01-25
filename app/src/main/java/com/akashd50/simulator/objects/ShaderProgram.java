package com.akashd50.simulator.objects;

import android.content.Context;
import android.opengl.GLES30;

import com.akashd50.simulator.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderProgram {
    public static final String A_POSITION = "a_Position";
    public static final String A_COLOR = "a_Color";
    public static final String U_MATRIX = "u_Matrix";
    public static final String A_DIRECTIONVECTOR = "a_DirectionVector";
    public static final String V_COLOR = "v_Color";
    public static final String V_OPACITY = "v_Opacity";
    public static final String U_TEXTURE_UNIT = "u_TextureUnit";

    private int program;
    public ShaderProgram(Context context, int rId){
        int hashC = 0;
        String vs = "";
        String fs = "";
        try{
            InputStream ins = context.getResources().openRawResource(rId);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));
            String line = "";
            while((line = bufferedReader.readLine())!=null){

                if(line.compareTo("")!=0 && line.charAt(0)=='#') hashC++;

                if(hashC<=1){
                    vs+= "\n" +line;
                }else{
                    fs+=  "\n" +line;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println(vs);

        System.out.println("Fragment Shader");

        System.out.println(vs);
        program = generateShadersAndProgram(vs,fs);
    }

    public int getProgram(){return program;}

    private static int generateShadersAndProgram(String vs, String fs){
        int vertexShad = loadShader(GLES30.GL_VERTEX_SHADER,
                vs);
        int fragmentShad = loadShader(GLES30.GL_FRAGMENT_SHADER,
                fs);
        int mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShad);
        GLES30.glAttachShader(mProgram, fragmentShad);
        GLES30.glLinkProgram(mProgram);
        return mProgram;
    }

    private static int loadShader(int type, String shaderCode){
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }
}
