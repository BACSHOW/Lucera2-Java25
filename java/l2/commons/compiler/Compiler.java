/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.compiler;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import l2.commons.compiler.MemoryClassLoader;
import l2.commons.compiler.MemoryJavaFileManager;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Compiler {
    private static final Logger _log = LoggerFactory.getLogger(Compiler.class);
    private static final JavaCompiler pQ = new EclipseCompiler();
    private final DiagnosticListener<JavaFileObject> pR = new DefaultDiagnosticListener();
    private final StandardJavaFileManager pS = new EclipseFileManager(Locale.getDefault(), Charset.defaultCharset());
    private final MemoryClassLoader pT = new MemoryClassLoader();
    private final MemoryJavaFileManager pU = new MemoryJavaFileManager(this.pS, this.pT);

    public boolean compile(File ... fileArray) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("-Xlint:all");
        arrayList.add("-warn:none");
        arrayList.add("-g");
        StringWriter stringWriter = new StringWriter();
        JavaCompiler.CompilationTask compilationTask = pQ.getTask(stringWriter, this.pU, this.pR, arrayList, null, this.pS.getJavaFileObjects(fileArray));
        return compilationTask.call() != false;
    }

    public boolean compile(Collection<File> collection) {
        return this.compile(collection.toArray(new File[collection.size()]));
    }

    public MemoryClassLoader getClassLoader() {
        return this.pT;
    }

    private class DefaultDiagnosticListener
    implements DiagnosticListener<JavaFileObject> {
        private DefaultDiagnosticListener() {
        }

        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            _log.error(diagnostic.getSource().getName() + (diagnostic.getPosition() == -1L ? "" : ":" + diagnostic.getLineNumber() + "," + diagnostic.getColumnNumber()) + ": " + diagnostic.getMessage(Locale.getDefault()));
        }
    }
}

