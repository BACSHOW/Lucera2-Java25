/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.compiler;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class MemoryByteCode
extends SimpleJavaFileObject {
    private ByteArrayOutputStream pV;
    private final String className;

    public MemoryByteCode(String string, URI uRI) {
        super(uRI, JavaFileObject.Kind.CLASS);
        this.className = string;
    }

    @Override
    public OutputStream openOutputStream() {
        this.pV = new ByteArrayOutputStream();
        return this.pV;
    }

    public byte[] getBytes() {
        return this.pV.toByteArray();
    }

    @Override
    public String getName() {
        return this.className;
    }
}

