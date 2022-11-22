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

    private final DEUIWindow effectWindow;
    private final DEUIButton effectRender;
    private final DEUIButton effectStop;
    private final EUIImage testImage;
    private final EUIImage testImage2;
    private DEUITextMultilineInput fragmentShader;
    private ShaderProgram shader;

    public static void initialize() {
        Instance = new ShaderDebugger();
        BaseMod.subscribe(Instance);
    }

    private ShaderDebugger()
    {
        effectWindow = new DEUIWindow(WINDOW_ID);
        effectRender = new DEUIButton(RENDER_ID);
        effectStop = new DEUIButton(STOP_ID);
        testImage = new EUIImage(EUIRM.Images.cardPool.texture());
        testImage2 = new EUIImage(EUIRM.Images.cardPool.texture());
        testImage.setPosition(EUIBase.screenW(0.5f), EUIBase.screenH(0.5f));
        testImage2.setPosition(EUIBase.screenW(0.7f), EUIBase.screenH(0.7f));
    }

    @Override
    public void receiveImGui()
    {
        effectWindow.render(() -> {
            // Buffers can be expensive so we only create this if we have to
            if (fragmentShader == null)
            {
                fragmentShader = new DEUITextMultilineInput(INPUT_ID, 0, 700, 3000);
            }
            fragmentShader.render();
            ImGui.separator();
            effectRender.renderInline(this::compile);
            effectStop.render(() -> {
                        shader = null;
                    }
            );
        });
        renderShader();
    }

    protected void renderShader()
    {
        if (shader != null)
        {
            shader.setUniformf("u_time", EUI.time());
            EUI.addPostRender(s -> EUIRenderHelpers.drawWithShader(s, shader, testImage::tryRender));
            EUI.addPostRender(s -> EUIRenderHelpers.drawRainbow(s, testImage2::tryRender));
        }
    }

    protected void compile()
    {
        FileHandle vShader = Gdx.files.internal(EUIRenderHelpers.SHADER_VERTEX);
        String vShaderString = vShader.readString();
        ShaderProgram program = new ShaderProgram(vShaderString, fragmentShader.get());
        if (program.isCompiled())
        {
            shader = program;
            if (program.getLog().length() > 0) {
                System.out.println(program.getLog());
            }
        }
        else
        {
            System.err.println(program.getLog());
            shader = null;
        }
    }
}
