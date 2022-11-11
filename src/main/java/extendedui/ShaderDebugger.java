package extendedui;

import basemod.BaseMod;
import basemod.interfaces.ImGuiSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import extendedui.debug.DEUIButton;
import extendedui.debug.DEUITextMultilineInput;
import extendedui.debug.DEUIUtils;
import extendedui.debug.DEUIWindow;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.AdvancedHitbox;
import imgui.ImGui;

public class ShaderDebugger implements ImGuiSubscriber
{
    protected static ShaderDebugger Instance;
    protected static String WINDOW_ID = "Shaders";
    protected static String INPUT_ID = "##FragmentShader";
    protected static String RENDER_ID = "Render";
    protected static String STOP_ID = "Stop";

    private final DEUIWindow EffectWindow;
    private final DEUIButton EffectRender;
    private final DEUIButton EffectStop;
    private final EUIImage TestImage;
    private final EUIImage TestImage2;
    private DEUITextMultilineInput FragmentShader;
    private ShaderProgram Shader;

    public static void Initialize() {
        Instance = new ShaderDebugger();
        BaseMod.subscribe(Instance);
    }

    private ShaderDebugger()
    {
        EffectWindow = new DEUIWindow(WINDOW_ID);
        EffectRender = new DEUIButton(RENDER_ID);
        EffectStop = new DEUIButton(STOP_ID);
        TestImage = new EUIImage(EUIRM.Images.CardPool.Texture());
        TestImage2 = new EUIImage(EUIRM.Images.CardPool.Texture());
        TestImage.SetPosition(EUIBase.ScreenW(0.5f), EUIBase.ScreenH(0.5f));
        TestImage2.SetPosition(EUIBase.ScreenW(0.7f), EUIBase.ScreenH(0.7f));
    }

    @Override
    public void receiveImGui()
    {
        EffectWindow.Render(() -> {
            // Buffers can be expensive so we only create this if we have to
            if (FragmentShader == null)
            {
                FragmentShader = new DEUITextMultilineInput(INPUT_ID, 0, 700, 3000);
            }
            FragmentShader.Render();
            ImGui.separator();
            EffectRender.RenderInline(this::Compile);
            EffectStop.Render(() -> {
                        Shader = null;
                    }
            );
        });
        RenderShader();
    }

    protected void RenderShader()
    {
        if (Shader != null)
        {
            Shader.setUniformf("u_time", EUI.Time());
            EUI.AddPostRender(s -> EUIRenderHelpers.DrawWithShader(s, Shader, TestImage::TryRender));
            EUI.AddPostRender(s -> EUIRenderHelpers.DrawRainbow(s, TestImage2::TryRender));
        }
    }

    protected void Compile()
    {
        FileHandle vShader = Gdx.files.internal(EUIRenderHelpers.SHADER_VERTEX);
        String vShaderString = vShader.readString();
        ShaderProgram program = new ShaderProgram(vShaderString, FragmentShader.Get());
        if (program.isCompiled())
        {
            Shader = program;
            if (program.getLog().length() > 0) {
                System.out.println(program.getLog());
            }
        }
        else
        {
            System.err.println(program.getLog());
            Shader = null;
        }
    }
}
