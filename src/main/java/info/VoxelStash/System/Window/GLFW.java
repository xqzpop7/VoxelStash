package info.VoxelStash.System.Window;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.stb.STBImage.*;

import info.VoxelStash.Utils;

public class GLFW {

    private long window;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        // Set up an error callback. The default implementation will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW.
        glfwDefaultWindowHints();
        // We don't want an OpenGL context, so we specify the NO_API client API.
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // The window will stay hidden after creation.
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // The window will be resizable.

        // Create the window
        window = glfwCreateWindow(1280, 720, "VoxelStash", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window.");
        }

        // Set the window icon
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer fileBuffer = Utils.ioResourceToByteBuffer("/icon.png", 1024);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer image = stbi_load_from_memory(fileBuffer, w, h, comp, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load image for icon: " + stbi_failure_reason());
            }

            GLFWImage.Buffer images = GLFWImage.malloc(1);
            GLFWImage icon = GLFWImage.create().width(w.get(0)).height(h.get(0)).pixels(image);
            images.put(0, icon);

            glfwSetWindowIcon(window, images);

            stbi_image_free(image);
            org.lwjgl.system.MemoryUtil.memFree(fileBuffer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load window icon.", e);
        }

        // Set up a key callback. It will be called every time a key is pressed.
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(win, true); // We will detect this in the rendering loop.
            }
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                System.out.println("GLFW Input: Key '" + glfwGetKeyName(key, scancode) + "' pressed.");
            }
        });

        // Get the resolution of the primary monitor to center the window.
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode != null) {
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - 1280) / 2,
                    (vidmode.height() - 720) / 2
            );
        }

        // Make the window visible.
        glfwShowWindow(window);
    }

    private void loop() {
        // Run the event loop until the user has attempted to close the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            // Poll for window events. The key callback will only be invoked during this call.
            glfwPollEvents();
        }
    }

    private void cleanup() {
        // Destroy the window.
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback.
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        System.out.println("GLFW resources cleaned up.");
    }
}
