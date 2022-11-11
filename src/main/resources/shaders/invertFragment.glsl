varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
  vec4 c = v_color * texture(u_texture, v_texCoords);
  gl_FragColor = vec4(1 - c.r, 1 - c.g, 1 - c.b, c.a);
}