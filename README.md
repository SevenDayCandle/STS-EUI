# STS-Effekseer
Utility library for playing Effekseer animations in Slay the Spire

**Requirements**
- BaseMod (5.37.1+)
- ModTheSpire (3.2.2+)

**How to Use**

To start an animation, call STSEffekseerManager.Play. This should ideally be done in an AbstractGameEffect when it is first created:

        Vector2 position = new Vector2(x,y); // Coordinates to play your effect at
        Vector3 rotation = new Vector3(0,0,0); // How much the animation should be rotated
        String key; // Internal path to your .efk animation
        int handle = STSEffekseerManager.Play(key, position, rotation);

This call returns an Effekseer handle that you can use to check on the status of the animation or modify its parameters. The following command below executes a command when your animation finishes. 

    public void update() {
            if (handle != null && !STSEffekseerManager.Exists(handle)) {
                //Do something here when your animation completes
            }
    }

To render your animation, call:

    public void render(SpriteBatch sb)
    {
        STSEffekseerManager.Render(sb);
    }

**Notes**
- Currently, this library only works with 64-bit Windows machines

**Credits**
- Code adapted from https://github.com/SrJohnathan/gdx-effekseer
- Libraries created from https://github.com/effekseer/EffekseerForMultiLanguages
