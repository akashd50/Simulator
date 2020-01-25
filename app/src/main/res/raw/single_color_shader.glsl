#version 300 es
uniform mat4 u_Matrix;
in vec4 a_Position;
void main()
{
    gl_Position = u_Matrix * a_Position;
}

#version 300 es
precision mediump float;
uniform float v_Opacity;
uniform vec3 v_Color;
layout (location=0) out vec4 fragColor;
void main()
{
    fragColor = vec4(v_Color,v_Opacity);
}