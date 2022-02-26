# STS-Effekseer
STS-Effekseer is a utility library for playing Effekseer animations in Slay the Spire.
It also comes with support for colorizing Effekseer animations and images in general with the colorful-gdx library, which offers a greater degree of freedom with color manipulation.


## **Requirements**
- BaseMod (5.37.1+)
- ModTheSpire (3.2.2+)

In addition, this library only works on Windows and Linux machines.

## **How to Use**

**Setting up your animations**

This library reads Effekseer animations exported with the .efk format. In Effekseer 1.62, you can create .efk files by using the Files -> Export -> Default Format command. Any images or subfolders that the effect needs to load must be in the same folder as your .efk file.

**Animations**

To start an animation, call STSEffekseerManager.Play. This should ideally be done in an AbstractGameEffect when it is first created:
```
        Vector2 position = new Vector2(x,y); // Coordinates to play your effect at
        Vector3 rotation = new Vector3(0,0,0); // How much the animation should be rotated
        String key; // Internal path to your .efk animation
        int handle = STSEffekseerManager.Play(key, position, rotation);
```
This call returns an Effekseer handle that you can use to check on the status of the animation or modify its parameters. The following command below executes a command when your animation finishes. 
```
    public void update() {
            if (handle != null && !STSEffekseerManager.Exists(handle)) {
                //Do something here when your animation completes
            }
    }
```
To render your animation, call:
```
    public void render(SpriteBatch sb)
    {
        STSEffekseerManager.Render(sb);

        // The following call below will render the animation colored Red
        Color color = Color.RED;
        STSEffekseerManager.RenderColorized(sb, colorizeColor);
    }
```

**Other Rendering Utilities**

This library also comes with wrappers to expedite rendering images with shader programs, colors, or blending functions:

```
    public void render(SpriteBatch sb)
    {
        Color color = Color.RED;
        STSRenderUtils.BlendingMode mode = STSRenderUtils.BlendingMode.Glowing;
        STSRenderUtils.DrawBrighter(sb, color, (spritebatch) -> YourRenderingFunction(spritebatch, ...)); // Uses colorful-gdx's RBGA colorspace to allow for brightening images
        STSRenderUtils.DrawColored(sb, color, (spritebatch) -> YourRenderingFunction(spritebatch, ...)); // Uses the default libgdx coloring method
        STSRenderUtils.DrawColorized(sb, color, (spritebatch) -> YourRenderingFunction(spritebatch, ...)); // Uses colorful-gdx's colorize shader to achieve better colorization
        STSRenderUtils.DrawGrayscale(sb, (spritebatch) -> YourRenderingFunction(spritebatch, ...)); // Draw an image in black-and-white
        STSRenderUtils.DrawWithShader(sb, shader, (spritebatch) -> YourRenderingFunction(spritebatch, ...)); // Use any shader you want to render an image
        STSRenderUtils.DrawGlowing(sb, (spritebatch) -> YourRenderingFunction(spritebatch, ...)); // Use a "color dodge" blending mode to render your image
        STSRenderUtils.DrawBlended(sb, mode, (spritebatch) -> YourRenderingFunction(spritebatch, ...)); // Use any available mode to render your image
    }
```


## **Credits**
- Code adapted from https://github.com/SrJohnathan/gdx-effekseer
- DLL Libraries created from https://github.com/effekseer/EffekseerForMultiLanguages
- Uses functions from https://github.com/tommyettinger/colorful-gdx

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
