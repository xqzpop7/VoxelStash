package info.VoxelStash.System.Log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public final class Root {
    private static final Logger LOGGER = LogManager.getRootLogger();

    public static class LogMessage {
        public static void info(String message) {
            LOGGER.debug(message);
        }

        public static void debug(String message) {
            LOGGER.debug(message);
        }

        public static void warn(String message) {
            LOGGER.warn(message);
        }

        public static void error(String message) {
            LOGGER.warn(message);
        }
    }

    public static class LogException {
        public static void IllegalState(String message) throws IllegalStateException {
            IllegalStateException exception = new IllegalStateException(message);
            LOGGER.error(message, exception);
            throw new IllegalStateException(message);
        }

        public static void RunTime(String message) throws RuntimeException {
            RuntimeException exception = new RuntimeException(message);
            LOGGER.error(message, exception);
            throw new RuntimeException(message);
        }

        public static void InputOutput(String message) throws IOException {
            IOException exception = new IOException(message);
            LOGGER.error(message, exception);
            throw new IOException(message);
        }
    }

    public static class LogGLFWErrorCallback {
        // Install before glfwInit()
        public static void Create() {
            try (GLFWErrorCallback callback = GLFWErrorCallback.create((error, descriptionPtr) -> {
                String description = GLFWErrorCallback.getDescription(descriptionPtr);
                // Map GLFW error code to log level as you prefer
                switch (error) {
                    case GLFW_NOT_INITIALIZED:
                    case GLFW_NO_CURRENT_CONTEXT:
                    case GLFW_INVALID_ENUM:
                    case GLFW_INVALID_VALUE:
                    case GLFW_OUT_OF_MEMORY:
                    case GLFW_API_UNAVAILABLE:
                    case GLFW_PLATFORM_ERROR:
                    case GLFW_FORMAT_UNAVAILABLE:
                    case GLFW_NO_WINDOW_CONTEXT:
                        LogMessage.error("GLFW error " + error + ": " + description);
                        break;
                    default:
                        LogMessage.warn("GLFW error " + error + ": " + description);
                }
            })) {
                callback.set();
            }
        }

        // Call during shutdown
        public static void Free() {
            try (GLFWErrorCallback old = glfwSetErrorCallback(null)) {
                if (old != null) old.free();
            }
        }
    }
}
