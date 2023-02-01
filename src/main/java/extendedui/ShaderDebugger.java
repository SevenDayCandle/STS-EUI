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
import imgui.flag.ImGuiInputTextFlags;

public class ShaderDebugger implements ImGuiSubscriber
{
    protected static ShaderDebugger instance;
    private static final String WINDOW_ID = "Shaders";
    private static final String INPUT_ID = "##FragmentShader";
    private static final String RENDER_ID = "Render";
    private static final String STOP_ID = "Stop";

    private final DEUIWindow effectWindow;
    private final DEUIButton effectRender;
    private final DEUIButton effectStop;
    private final EUIImage testImage;
    private DEUITextMultilineInput fragmentShader;
    private ShaderProgram shader;

    private ShaderDebugger()
    {
        effectWindow = new DEUIWindow(WINDOW_ID);
        effectRender = new DEUIButton(RENDER_ID);
        effectStop = new DEUIButton(STOP_ID);
        testImage = new EUIImage(EUIRM.images.baseBadge.texture());
        testImage.setPosition(EUIBase.screenW(0.5f), EUIBase.screenH(0.5f));
    }

    public static void initialize() {
        try
        {
            instance = new ShaderDebugger();
            BaseMod.subscribe(instance);
        }
        catch (Exception e)
        {
            EUIUtils.logInfoIfDebug(ShaderDebugger.class, "Unable to load shader debugger");
        }
    }

    @Override
    public void receiveImGui()
    {
        effectWindow.render(() -> {
            effectRender.renderInline(this::compile);
            effectStop.render(() -> shader = null);
            ImGui.separator();
            if (fragmentShader == null)
            {
                fragmentShader = new DEUITextMultilineInput(INPUT_ID, 0, 0, 3000, ImGuiInputTextFlags.AllowTabInput);
            }
            fragmentShader.render();
        });
        renderShader();
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

    protected void renderShader()
    {
        if (shader != null)
        {
            shader.setUniformf("u_time", EUI.time());
            EUI.addPostRender(s -> EUIRenderHelpers.drawWithShader(s, shader, testImage::tryRender));
        }
    }
}
