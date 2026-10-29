/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.compiler;

import java.util.HashMap;
import java.util.Map;
import l2.commons.compiler.MemoryByteCode;

public class MemoryClassLoader
extends ClassLoader {
    private final Map<String, MemoryByteCode> classes = new HashMap<String, MemoryByteCode>();
    private final Map<String, MemoryByteCode> pW = new HashMap<String, MemoryByteCode>();

    @Override
    protected Class<?> findClass(String string) throws ClassNotFoundException {
        MemoryByteCode memoryByteCode = this.classes.get(string);
        if (memoryByteCode == null && (memoryByteCode = this.classes.get(string)) == null) {
            return super.findClass(string);
        }
        return this.defineClass(string, memoryByteCode.getBytes(), 0, memoryByteCode.getBytes().length);
    }

    public void addClass(MemoryByteCode memoryByteCode) {
        this.classes.put(memoryByteCode.getName(), memoryByteCode);
        this.pW.put(memoryByteCode.getName(), memoryByteCode);
    }

    public MemoryByteCode getClass(String string) {
        return this.classes.get(string);
    }

    public String[] getLoadedClasses() {
        return this.pW.keySet().toArray(new String[this.pW.size()]);
    }

    public void clear() {
        this.pW.clear();
    }
}

