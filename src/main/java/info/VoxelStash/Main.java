package info.VoxelStash;

import info.VoxelStash.System.Window.GLFW;

/**
 * The Main class serves as the entry point.
 * It simply prints out the command-line arguments.
 *
 * @author FruityPop
 */
public class Main {
    /**
     * The main entry point of the program.
     *
     * @param args command-line arguments
     */
    static void main(String[] args) {
        if (args.length > 0) {
            System.out.println("Your arg/s: was/were" + args[0]);
        } else {
            System.out.println("You didn't specify any arguments.");
        }

        new GLFW().run();
    }
}
