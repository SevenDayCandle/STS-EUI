package stseffekseer;

import Effekseer.swig.EffekseerEffectCore;
import Effekseer.swig.EffekseerTextureType;
import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Locale;

/* Adapted from https://github.com/SrJohnathan/gdx-effekseer */

class STSEffekSeerUtils {
    public static final float DEFAULT_MAGNIFICATION = 5f;
    private static final int MIN_PREFIX_LENGTH = 3;
    private static final String LIBRARY_NAME = "EffekseerNativeForJava";
    private static final String PATH_PREFIX = "STSEffekSeerUtils";
    private static File temporaryDir;

    public static EffekseerEffectCore LoadEffect(String effectPath) throws Exception {
        return LoadEffect(effectPath, DEFAULT_MAGNIFICATION);
    }

    public static EffekseerEffectCore LoadEffect(String effectPath, float magnification) throws Exception {
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
        EffekseerTextureType[] textureTypes = new EffekseerTextureType[] {
                EffekseerTextureType.Color,
                EffekseerTextureType.Normal,
                EffekseerTextureType.Distortion,
        };

        for(int t = 0; t < 3; t++)
        {
            for (int i = 0; i < effectCore.GetTextureCount(textureTypes[t]); i++) {
                String path = (new File(effectPath)).getParent();
                if (path != null) {
                    path += "/" + effectCore.GetTexturePath(i, textureTypes[t]);
                } else {
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
            } else {
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
            } else {
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
            } else {
                path = effectCore.GetCurvePath(i);
            }

            handle = Gdx.files.internal(path);
            byte[] bytes = handle.readBytes();
            effectCore.LoadCurve(bytes, bytes.length, i);
        }

        return effectCore;
    }

    protected static String GetEffekseerPath() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if (OS.contains("win")) {
            return "/" + LIBRARY_NAME + ".dll";
        }
        //TODO Add files for other OS
        throw new RuntimeException("Unsupported OS");
    }

    protected static void LoadLibraryFromJar() throws IOException {
        LoadLibraryFromJar(GetEffekseerPath());
    }

    protected static void LoadLibraryFromJar(String path) throws IOException {

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
            temporaryDir = CreateTemporaryDirectory(PATH_PREFIX);
            temporaryDir.deleteOnExit();
        }

        File temp = new File(temporaryDir, filename);

        try (InputStream is = STSEffekSeerUtils.class.getResourceAsStream(path)) {
            Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            temp.delete();
            throw e;
        } catch (NullPointerException e) {
            temp.delete();
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }

        try {
            System.load(temp.getAbsolutePath());
        } finally {
            if (IsPosixCompliant()) {
                // Assume POSIX compliant file system, can be deleted after loading
                temp.delete();
            } else {
                // Assume non-POSIX, and don't delete until last file descriptor closed
                temp.deleteOnExit();
            }
        }
    }

    private static boolean IsPosixCompliant() {
        try {
            return FileSystems.getDefault()
                    .supportedFileAttributeViews()
                    .contains("posix");
        } catch (FileSystemNotFoundException
                | ProviderNotFoundException
                | SecurityException e) {
            return false;
        }
    }

    private static File CreateTemporaryDirectory(String prefix) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File generatedDir = new File(tempDir, prefix + System.nanoTime());

        if (!generatedDir.mkdir())
            throw new IOException("Failed to create temp directory " + generatedDir.getName());

        return generatedDir;
    }
}