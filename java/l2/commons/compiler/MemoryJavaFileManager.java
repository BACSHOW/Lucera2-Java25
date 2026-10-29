/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.compiler;

import java.io.IOException;
import java.net.URI;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import l2.commons.compiler.MemoryByteCode;
import l2.commons.compiler.MemoryClassLoader;

public class MemoryJavaFileManager
extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private MemoryClassLoader pX;

    public MemoryJavaFileManager(StandardJavaFileManager standardJavaFileManager, MemoryClassLoader memoryClassLoader) {
        super(standardJavaFileManager);
        this.pX = memoryClassLoader;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String string, JavaFileObject.Kind kind, FileObject fileObject) throws IOException {
        MemoryByteCode memoryByteCode = new MemoryByteCode(string.replace('/', '.').replace('\\', '.'), URI.create("file:///" + string.replace('.', '/').replace('\\', '/') + kind.extension));
        this.pX.addClass(memoryByteCode);
        return memoryByteCode;
    }

    @Override
    public ClassLoader getClassLoader(JavaFileManager.Location location) {
        return this.pX;
    }
}

