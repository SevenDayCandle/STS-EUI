package extendedui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import extendedui.swig.EffekseerEffectCore;
import extendedui.swig.EffekseerTextureType;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/* Adapted from https://github.com/SrJohnathan/gdx-effekseer */

public class STSEffekSeerUtils {
    private static final int MIN_PREFIX_LENGTH = 3;
    private static final String LIBRARY_NAME = "EffekseerNativeForJava";
    private static final String PATH_PREFIX = "STSEffekSeerUtils";
    private static final float DEFAULT_MAGNIFICATION = 50f;
    private static final float EFFEKSEER_COLOR_RATE = 255f;
    private static File temporaryDir;

    private static File createTemporaryDirectory(String prefix) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File generatedDir = new File(tempDir, prefix + System.nanoTime());

        if (!generatedDir.mkdir()) {
            throw new IOException("Failed to create temp directory " + generatedDir.getName());
        }

        return generatedDir;
    }

    private static String getEffekseerPath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "/" + LIBRARY_NAME + ".dll";
        }
        else if (SystemUtils.IS_OS_LINUX) {
            return "/" + LIBRARY_NAME + ".so";
        }
        throw new RuntimeException("Unsupported OS");
    }

    public static EffekseerEffectCore loadEffect(String effectPath) {
        return loadEffect(effectPath, DEFAULT_MAGNIFICATION);
    }

    public static EffekseerEffectCore loadEffect(String effectPath, float magnification) {
        com.badlogic.gdx.files.FileHandle handle = Gdx.files.internal(effectPath);
        EffekseerEffectCore effectCore = new EffekseerEffectCore();

        // load an effect
        {
            byte[] bytes = handle.readBytes();
            if (!effectCore.Load(bytes, bytes.length, magnification)) {
                System.out.print("Failed to load.");
                return null;
            }
        }

        // load textures
        EffekseerTextureType[] textureTypes = new EffekseerTextureType[]{
                EffekseerTextureType.Color,
                EffekseerTextureType.Normal,
                EffekseerTextureType.Distortion,
                };

        for (int t = 0; t < 3; t++) {
            for (int i = 0; i < effectCore.GetTextureCount(textureTypes[t]); i++) {
                String path = (new File(effectPath)).getParent();
                if (path != null) {
                    path += "/" + effectCore.GetTexturePath(i, textureTypes[t]);
                }
                else {
                    path = effectCore.GetTexturePath(i, textureTypes[t]);
                }

                handle = Gdx.files.internal(path);
                byte[] bytes = handle.readBytes();
                effectCore.LoadTexture(bytes, bytes.length, i, textureTypes[t]);
            }
        }

        for (int i = 0; i < effectCore.GetModelCount(); i++) {
            String path = (new File(effectPath)).getParent();
            if (path != null) {
                path += "/" + effectCore.GetModelPath(i);
            }
            else {
                path = effectCore.GetModelPath(i);
            }

            handle = Gdx.files.internal(path);
            byte[] bytes = handle.readBytes();
            effectCore.LoadModel(bytes, bytes.length, i);
        }

        for (int i = 0; i < effectCore.GetMaterialCount(); i++) {
            String path = (new File(effectPath)).getParent();
            if (path != null) {
                path += "/" + effectCore.GetMaterialPath(i);
            }
            else {
                path = effectCore.GetMaterialPath(i);
            }

            handle = Gdx.files.internal(path);
            byte[] bytes = handle.readBytes();
            effectCore.LoadMaterial(bytes, bytes.length, i);
        }

        for (int i = 0; i < effectCore.GetCurveCount(); i++) {
            String path = (new File(effectPath)).getParent();
            if (path != null) {
                path += "/" + effectCore.GetCurvePath(i);
            }
            else {
                path = effectCore.GetCurvePath(i);
            }

            handle = Gdx.files.internal(path);
            byte[] bytes = handle.readBytes();
            effectCore.LoadCurve(bytes, bytes.length, i);
        }

        return effectCore;
    }

    protected static void loadLibraryFromJar() throws Exception, UnsatisfiedLinkError {
        loadLibraryFromJar(getEffekseerPath());
    }

    protected static void loadLibraryFromJar(String path) throws Exception, UnsatisfiedLinkError {

        if (null == path || !path.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }

        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

        // Check if the filename is okay
        if (filename == null || filename.length() < MIN_PREFIX_LENGTH) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }

        // Prepare temporary file
        if (temporaryDir == null) {
            temporaryDir = createTemporaryDirectory(PATH_PREFIX);
            temporaryDir.deleteOnExit();
        }

        File temp = new File(temporaryDir, filename);

        try (InputStream is = STSEffekSeerUtils.class.getResourceAsStream(path)) {
            Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (AccessDeniedException ignored) {
            // If the file already exists (i.e. failed to delete last time), we should just try to load it again
        }
        catch (IOException e) {
            temp.delete();
            throw e;
        }
        catch (NullPointerException e) {
            temp.delete();
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }

        try {
            System.load(temp.getAbsolutePath());
        }
        finally {
            // We need to keep the DLL file until the program closes, in case the system needs to read from it again
            temp.deleteOnExit();
        }
    }

    /* Effekseer color values range from 0-255, whereas LibGDX color values range from 0-1 */
    public static float[] toEffekseerColor(Color color) {
        return color != null ?
                new float[]{
                        color.r * EFFEKSEER_COLOR_RATE,
                        color.g * EFFEKSEER_COLOR_RATE,
                        color.b * EFFEKSEER_COLOR_RATE,
                        color.a * EFFEKSEER_COLOR_RATE
                }
                : null;
    }
}