#version 430 core

in vec2 Uv;

uniform vec4 textColor = vec4(1,0,0, 1);

uniform sampler2D tex;


out vec4 Color;


void main() {
    vec4 texColor = texture(tex, Uv);

    //if (texColor.w == 0) discard;
    Color = vec4(texColor * textColor);
    //Color = vec4(Uv.x, 0, Uv.y, 1);
}