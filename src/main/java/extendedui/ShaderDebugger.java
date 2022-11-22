package extendedui;

import basemod.BaseMod;
import basemod.interfaces.ImGuiSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import extendedui.debug.DEUIButton;
import extendedui.debug.DEUITextMultilineInput;
import extendedui.debug.DEUIWindow;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIImage;
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

    public static void initialize() {
        Instance = new ShaderDebugger();
        BaseMod.subscribe(Instance);
    }

    private ShaderDebugger()
    {
        EffectWindow = new DEUIWindow(WINDOW_ID);
        EffectRender = new DEUIButton(RENDER_ID);
        EffectStop = new DEUIButton(STOP_ID);
        TestImage = new EUIImage(EUIRM.Images.CardPool.texture());
        TestImage2 = new EUIImage(EUIRM.Images.CardPool.texture());
        TestImage.setPosition(EUIBase.screenW(0.5f), EUIBase.screenH(0.5f));
        TestImage2.setPosition(EUIBase.screenW(0.7f), EUIBase.screenH(0.7f));
    }

    @Override
    public void receiveImGui()
    {
        EffectWindow.render(() -> {
            // Buffers can be expensive so we only create this if we have to
            if (FragmentShader == null)
            {
                FragmentShader = new DEUITextMultilineInput(INPUT_ID, 0, 700, 3000);
            }
            FragmentShader.render();
            ImGui.separator();
            EffectRender.renderInline(this::compile);
            EffectStop.render(() -> {
                        Shader = null;
                    }
            );
        });
        renderShader();
    }

    protected void renderShader()
    {
        if (Shader != null)
        {
            Shader.setUniformf("u_time", EUI.time());
            EUI.addPostRender(s -> EUIRenderHelpers.drawWithShader(s, Shader, TestImage::tryRender));
            EUI.addPostRender(s -> EUIRenderHelpers.drawRainbow(s, TestImage2::tryRender));
        }
    }

    protected void compile()
    {
        FileHandle vShader = Gdx.files.internal(EUIRenderHelpers.SHADER_VERTEX);
        String vShaderString = vShader.readString();
        ShaderProgram program = new ShaderProgram(vShaderString, FragmentShader.get());
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
