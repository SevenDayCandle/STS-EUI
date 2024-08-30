varying vec2 v_texCoords;
varying vec4 v_color;

uniform float lRed;
uniform float lGreen;
uniform float lBlue;
uniform float lAlpha;
uniform float rRed;
uniform float rGreen;
uniform float rBlue;
uniform float rAlpha;
uniform float anchorAR;
uniform float anchorAG;
uniform float anchorAB;
uniform float anchorBR;
uniform float anchorBG;
uniform float anchorBB;

uniform sampler2D u_texture;
uniform vec2 u_screenSize;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);

    vec3 T = vec3(color.r, color.g, color.b);
    vec3 aA = vec3(anchorAR, anchorAG, anchorAB);
    vec3 aB = vec3(anchorBR, anchorBG, anchorBB);

    float lT = length(T);

    float distA = 0.2126*abs(aA.r - T.r) + 0.7152*abs(aA.g - T.g) + 0.0722*abs(aA.b - T.b);
    float distB = 0.2126*abs(aB.r - T.r) + 0.7152*abs(aB.g - T.g) + 0.0722*abs(aB.b - T.b);

    float vT = distA/(distB+distA);
    float orig = max(0, 1 - lAlpha - rAlpha);

    float newR = (lRed * lAlpha + (rRed - lRed)*vT * rAlpha) + (orig * T.r);
    float newG = (lGreen * lAlpha + (rGreen - lGreen)*vT * rAlpha) + (orig * T.g);
    float newB = (lBlue * lAlpha + (rBlue - lBlue)*vT * rAlpha) + (orig * T.b);

    vec3 newColor = vec3(newR, newG, newB)*lT;

    gl_FragColor = vec4(newColor, color.a);
}