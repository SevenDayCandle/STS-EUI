// Adapted from https://github.com/cacheflowe/haxademic/blob/master/data/haxademic/shaders/filters/colorize.glsl
// Variant of colorize shader with transparency taken from texture, which is needed to colorize effects rendered with glowing blending modes or skeletons

#ifdef GL_ES
#define LOWP lowpprecision mediump float;
#else
#define LOWP
#endifvarying

varying vec2 v_texCoords;
varying LOWP vec4 v_color;
uniform sampler2D u_texture;

void main() {
    vec4 tgt = texture2D(u_texture, v_texCoords);
    gl_FragColor = clamp(vec4(tgt.rgb * pow((v_color.rgb + 0.1) * 1.666, vec3(1.5)), v_color.a * tgt.a), 0.0, 1.0);
}