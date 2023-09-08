// Adapted from https://gist.github.com/xoppa/33589b7d5805205f8f08
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_border = 1.0 / 128.0;
uniform vec3 u_borderColor;
const vec3 black = vec3(0.0,0.0,0.0);
void main()
{
    vec4 col = texture2D(u_texture, v_texCoords);
    if (col.a > 0.5) {
        float a = 1 - col.a;
        gl_FragColor = vec4(mix(black, u_borderColor, a), 1.0);
    }
    else {
        float a = texture2D(u_texture, vec2(v_texCoords.x + u_border, v_texCoords.y)).a +
        texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - u_border)).a +
        texture2D(u_texture, vec2(v_texCoords.x - u_border, v_texCoords.y)).a +
        texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + u_border)).a;
        if (col.a < 1.0 && a > 0.0)
        gl_FragColor = vec4(u_borderColor, clamp(a * 1.5, 0.0, 1.0));
        else
        gl_FragColor = col;
    }
}