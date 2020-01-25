#version 300 es
uniform mat4 u_Matrix;
uniform float u_Time;
in vec3 a_Position;
in vec3 a_Color;
in vec3 a_DirectionVector;
in vec3 a_accVector;
in float a_ParticleStartTime;
out vec3 v_Color;
out float v_ElapsedTime;
void main(){
    v_Color = a_Color;
    v_ElapsedTime = u_Time - a_ParticleStartTime;
    vec3 currentPosition = a_Position + a_DirectionVector*v_ElapsedTime;
    gl_Position = u_Matrix * vec4(currentPosition, 1.0);
    gl_PointSize = 10.0;
}

#version 300 es
precision mediump float;
uniform sampler2D u_TextureUnit;
in vec3 v_Color;
in float v_ElapsedTime;
out vec4 gl_FragColor;
void main(){
        gl_FragColor = vec4(v_Color,1.0);
}