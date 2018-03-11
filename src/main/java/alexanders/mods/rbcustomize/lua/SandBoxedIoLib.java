package alexanders.mods.rbcustomize.lua;

import org.luaj.vm2.lib.jse.JseIoLib;

import java.io.IOException;

public class SandBoxedIoLib extends JseIoLib {
    @Override
    protected File openFile(String filename, boolean readMode, boolean appendMode, boolean updateMode, boolean binaryMode) throws IOException {
        String newPath = new java.io.File("./rockbottom", new java.io.File(filename).getAbsolutePath()).getAbsolutePath(); //TODO: Make this less hacky?
        return super.openFile(newPath, readMode, appendMode, updateMode, binaryMode);
    }

    @Override
    protected File openProgram(String prog, String mode) throws IOException {
        throw new UnsupportedOperationException("A script tried to call an external program this is not allowed");
    }
}
