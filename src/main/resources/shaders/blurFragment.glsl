// Adapted from https://github.com/mattdesl/lwjgl-basics/wiki/ShaderLesson5
// This is actually identical to the unused blur fragment already contained in Slay the Spire, but the variable names have been fixed to use the coloringVertex variables

//"in" attributes from our vertex shader
varying vec4 v_color;
varying vec2 v_texCoords;

//declare uniforms
uniform sampler2D u_texture;
//the amount to blur, i.e. how far off center to sample from
//1.0 -> blur by one pixel
//2.0 -> blur by two pixels, etc.
uniform float u_radius;
uniform vec2 u_dir;

void main() {
    //this will be our RGBA sum
    vec4 sum = vec4(0.0);

    //our original texcoord for this fragment
    vec2 tc = v_texCoords;

    //the direction of our blur
    //(1.0, 0.0) -> x-axis blur
    //(0.0, 1.0) -> y-axis blur
    float hstep = u_dir.x;
    float vstep = u_dir.y;

    //apply blurring, using a 9-tap filter with predefined gaussian weights

    sum += texture2D(u_texture, vec2(tc.x - 4.0*u_radius*hstep, tc.y - 4.0*u_radius*vstep)) * 0.0162162162;
    sum += texture2D(u_texture, vec2(tc.x - 3.0*u_radius*hstep, tc.y - 3.0*u_radius*vstep)) * 0.0540540541;
    sum += texture2D(u_texture, vec2(tc.x - 2.0*u_radius*hstep, tc.y - 2.0*u_radius*vstep)) * 0.1216216216;
    sum += texture2D(u_texture, vec2(tc.x - 1.0*u_radius*hstep, tc.y - 1.0*u_radius*vstep)) * 0.1945945946;

    sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.2270270270;

    sum += texture2D(u_texture, vec2(tc.x + 1.0*u_radius*hstep, tc.y + 1.0*u_radius*vstep)) * 0.1945945946;
    sum += texture2D(u_texture, vec2(tc.x + 2.0*u_radius*hstep, tc.y + 2.0*u_radius*vstep)) * 0.1216216216;
    sum += texture2D(u_texture, vec2(tc.x + 3.0*u_radius*hstep, tc.y + 3.0*u_radius*vstep)) * 0.0540540541;
    sum += texture2D(u_texture, vec2(tc.x + 4.0*u_radius*hstep, tc.y + 4.0*u_radius*vstep)) * 0.0162162162;

    //discard alpha for our simple demo, multiply by vertex color and return
    gl_FragColor = v_color * vec4(sum.rgb, v_color.a);
}
