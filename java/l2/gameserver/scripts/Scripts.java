/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.scripts;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import l2.commons.compiler.Compiler;
import l2.commons.compiler.MemoryClassLoader;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scripts {
	private static final Logger _log = LoggerFactory.getLogger(Scripts.class);
	private static final Scripts aod = new Scripts();
	public static final Map<Integer, List<ScriptClassAndMethod>> dialogAppends = new HashMap<Integer, List<ScriptClassAndMethod>>();
	public static final Map<String, ScriptClassAndMethod> onAction = new HashMap<String, ScriptClassAndMethod>();
	public static final Map<String, ScriptClassAndMethod> onActionShift = new HashMap<String, ScriptClassAndMethod>();
	private final Compiler aoe = new Compiler();
	private final Map<String, Class<?>> aof = new TreeMap();

		private static Class<?> resolveScriptClass(String className, MemoryClassLoader memoryClassLoader) {
		try {
			return Class.forName(className, false, Scripts.class.getClassLoader());
		} catch (ClassNotFoundException ignored) {
		}
		ClassLoader contextCl = Thread.currentThread().getContextClassLoader();
		if (contextCl != null && contextCl != Scripts.class.getClassLoader()) {
			try {
				return Class.forName(className, false, contextCl);
			} catch (ClassNotFoundException ignored) {
			}
		}
		if (memoryClassLoader != null) {
			try {
				return memoryClassLoader.loadClass(className);
			} catch (ClassNotFoundException ignored) {
			}
		}
		return null;
	}

	private static Object instantiateScript(Class<?> clazz) throws Exception {
		try {
			java.lang.reflect.Constructor<?> ctor = clazz.getDeclaredConstructor();
			ctor.setAccessible(true);
			return ctor.newInstance();
		} catch (NoSuchMethodException e) {
			return clazz.newInstance();
		}
	}

	public static final Scripts getInstance() {
		return aod;
	}

	private Scripts() {
		this.load();
		this.du();
	}

	/*
	 * WARNING - Removed try catching itself - possible behaviour change.
	 */
	private void load() {
		Object object;
		_log.info("Scripts: Loading...");
		ArrayList arrayList = new ArrayList();
		boolean bl = false;
        File file = new File(System.getProperty("l2.scripts.jar", "../libs/scripts.jar"));
		if (file.exists()) {
			object = null;
			MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
			try {
				object = new JarInputStream(new FileInputStream(file));
				JarEntry jarEntry = null;
				while ((jarEntry = ((JarInputStream) object).getNextJarEntry()) != null) {
					String entryName = jarEntry.getName();
					if (entryName.contains(ClassUtils.INNER_CLASS_SEPARATOR) || !entryName.endsWith(".class")) {
						continue;
					}
					String string = entryName.replace(".class", "").replace("/", ".");
					Class<?> clazz = resolveScriptClass(string, memoryClassLoader);
					if (clazz == null || Modifier.isAbstract(clazz.getModifiers())) {
						continue;
					}
					arrayList.add(clazz);
				}
				bl = true;
			} catch (Exception exception) {
				try {
					_log.error("Fail to load scripts.jar!", exception);
					arrayList.clear();
				} catch (Throwable throwable) {
					IOUtils.closeQuietly((java.io.Closeable) object);
					throw throwable;
				}
				IOUtils.closeQuietly((InputStream) object);
			}
			IOUtils.closeQuietly((InputStream) object);
		}
		if (!bl) {
			bl = this.a(arrayList, "");
		}
		if (!bl) {
			_log.error("Scripts: Failed loading scripts!");
			Runtime.getRuntime().exit(0);
			return;
		}
		_log.info("Scripts: Loaded " + arrayList.size() + " classes.");
		for (int i = 0; i < arrayList.size(); ++i) {
			object = (Class) arrayList.get(i);
			this.aof.put(((Class) object).getName(), (Class<?>) object);
		}
	}

	/*
	 * WARNING - Removed try catching itself - possible behaviour change.
	 */
	private void du() {
		_log.info("Extensions: Loading...");
		ArrayList arrayList = new ArrayList();
		boolean bl = false;
		File[] fileArray = new File(".").listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".ext.jar");
			}
		});
		Object object = fileArray;
		int n = ((File[]) object).length;
		for (int i = 0; i < n; ++i) {
			File file = ((File[]) object)[i];
			if (!file.exists())
				continue;
			JarInputStream jarInputStream = null;
			MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
			try {
				jarInputStream = new JarInputStream(new FileInputStream(file));
				JarEntry jarEntry = null;
				while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
					String entryName = jarEntry.getName();
					if (entryName.startsWith("java/") || entryName.startsWith("sun/")
							|| entryName.startsWith("l2/authserver")
							|| entryName.startsWith("l2/commons")
							|| entryName.startsWith("l2/gameserver")
							|| entryName.contains(ClassUtils.INNER_CLASS_SEPARATOR)
							|| !entryName.endsWith(".class")) {
						continue;
					}
					String string = entryName.replace(".class", "").replace("/", ".");
					Class<?> clazz = resolveScriptClass(string, memoryClassLoader);
					if (clazz == null || Modifier.isAbstract(clazz.getModifiers())) {
						continue;
					}
					arrayList.add(clazz);
				}
				bl = true;
			} catch (Exception exception) {
				try {
					_log.error("Failed to load \"" + file + "\"!", exception);
					arrayList.clear();
				} catch (Throwable throwable) {
					IOUtils.closeQuietly(jarInputStream);
					throw throwable;
				}
				IOUtils.closeQuietly(jarInputStream);
				continue;
			}
			IOUtils.closeQuietly(jarInputStream);
			continue;
		}
		_log.info("Extensions: Loaded " + arrayList.size() + " extension classes.");
		for (n = 0; n < arrayList.size(); ++n) {
			object = (Class) arrayList.get(n);
			this.aof.put(((Class) object).getName(), (Class<?>) object);
		}
	}

	public void init() {
		for (Class<?> clazz : this.aof.values()) {
			int n;
			int n2;
			Class<?> clazz2;
			String string;
			this.g(clazz);
			if (Config.DONTLOADQUEST && ClassUtils.isAssignable(clazz, Quest.class)
					|| !ArrayUtils.isEmpty(Config.IGNORE_QUESTS) && ClassUtils.isAssignable(clazz, Quest.class)
							&& (string = (clazz2 = clazz).getSimpleName()).charAt(0) == '_'
							&& (n2 = string.indexOf(95, 1)) > 0
							&& ArrayUtils.contains(Config.IGNORE_QUESTS, n = Integer.parseInt(string.substring(1, n2)))
					|| !ClassUtils.isAssignable(clazz, ScriptFile.class))
				continue;
			try {
				((ScriptFile) instantiateScript(clazz)).onLoad();
			} catch (Exception exception) {
				_log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", exception);
			}
		}
	}

	public boolean reload() {
		_log.info("Scripts: Reloading...");
		return this.reload("");
	}

	public boolean reload(String string) {
		ArrayList arrayList = new ArrayList();
		if (!this.a(arrayList, string)) {
			_log.error("Scripts: Failed reloading script(s): " + string + "!");
			return false;
		}
		_log.info("Scripts: Reloaded " + arrayList.size() + " classes.");
		for (int i = 0; i < arrayList.size(); ++i) {
			Class clazz = (Class) arrayList.get(i);
			Class clazz2 = this.aof.put(clazz.getName(), clazz);
			if (clazz2 != null) {
				if (ClassUtils.isAssignable(clazz2, ScriptFile.class)) {
					try {
						((ScriptFile) instantiateScript(clazz2)).onReload();
					} catch (Exception exception) {
						_log.error("Scripts: Failed running " + clazz2.getName() + ".onReload()", exception);
					}
				}
				this.h(clazz2);
			}
			if (Config.DONTLOADQUEST && ClassUtils.isAssignable(clazz, Quest.class))
				continue;
			if (ClassUtils.isAssignable(clazz, ScriptFile.class)) {
				try {
					((ScriptFile) instantiateScript(clazz)).onLoad();
				} catch (Exception exception) {
					_log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", exception);
				}
			}
			this.g(clazz);
		}
		return true;
	}

	public void shutdown() {
		for (Class<?> clazz : this.aof.values()) {
			if (ClassUtils.isAssignable(clazz, Quest.class) || !ClassUtils.isAssignable(clazz, ScriptFile.class))
				continue;
			try {
				((ScriptFile) instantiateScript(clazz)).onShutdown();
			} catch (Exception exception) {
				_log.error("Scripts: Failed running " + clazz.getName() + ".onShutdown()", exception);
			}
		}
	}

	private boolean a(List<Class<?>> list, String string) {
		Collection<File> collection = Collections.emptyList();
		File file = new File(Config.DATAPACK_ROOT, "data/scripts/" + string.replace(".", "/") + ".java");
		if (file.isFile()) {
			collection = new ArrayList(1);
			collection.add(file);
		} else {
			file = new File(Config.DATAPACK_ROOT, "data/scripts/" + string);
			if (file.isDirectory()) {
				collection = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(".java"),
						FileFilterUtils.directoryFileFilter());
			}
		}
		if (collection.isEmpty()) {
			return false;
		}
		boolean bl = this.aoe.compile(collection);
		if (bl) {
			MemoryClassLoader memoryClassLoader = this.aoe.getClassLoader();
			for (String string2 : memoryClassLoader.getLoadedClasses()) {
				if (string2.contains(ClassUtils.INNER_CLASS_SEPARATOR))
					continue;
				try {
					Class<?> clazz = memoryClassLoader.loadClass(string2);
					if (Modifier.isAbstract(clazz.getModifiers()))
						continue;
					list.add(clazz);
				} catch (ClassNotFoundException classNotFoundException) {
					bl = false;
					_log.error("Scripts: Can't load script class: " + string2, classNotFoundException);
				}
			}
			memoryClassLoader.clear();
		}
		return bl;
	}

	private void g(Class<?> clazz) {
		try {
			for (Method method : clazz.getMethods()) {
				Object object;
				if (method.getName().contains("DialogAppend_")) {
					object = Integer.parseInt(method.getName().substring(13));
					List<ScriptClassAndMethod> list = dialogAppends.get(object);
					if (list == null) {
						list = new ArrayList<ScriptClassAndMethod>();
						dialogAppends.put((Integer) object, list);
					}
					list.add(new ScriptClassAndMethod(clazz.getName(), method.getName()));
					continue;
				}
				if (method.getName().contains("OnAction_")) {
					object = method.getName().substring(9);
					onAction.put((String) object, new ScriptClassAndMethod(clazz.getName(), method.getName()));
					continue;
				}
				if (!method.getName().contains("OnActionShift_"))
					continue;
				object = method.getName().substring(14);
				onActionShift.put((String) object, new ScriptClassAndMethod(clazz.getName(), method.getName()));
			}
		} catch (Exception exception) {
			_log.error("", exception);
		}
	}

	private void h(Class<?> clazz) {
		try {
			for (List<ScriptClassAndMethod> iterator : dialogAppends.values()) {
				ArrayList<ScriptClassAndMethod> arrayList = new ArrayList<ScriptClassAndMethod>();
				for (ScriptClassAndMethod scriptClassAndMethod : iterator) {
					if (!scriptClassAndMethod.className.equals(clazz.getName()))
						continue;
					arrayList.add(scriptClassAndMethod);
				}
				for (ScriptClassAndMethod scriptClassAndMethod : arrayList) {
					iterator.remove(scriptClassAndMethod);
				}
			}
			ArrayList<String> arrayList = new ArrayList();
			for (Map.Entry<String, ScriptClassAndMethod> entry : onAction.entrySet()) {
				if (!entry.getValue().className.equals(clazz.getName()))
					continue;
				arrayList.add(entry.getKey());
			}
			for (String string : arrayList) {
				onAction.remove(string);
			}
			arrayList = new ArrayList<String>();
			for (Map.Entry<String, ScriptClassAndMethod> entry : onActionShift.entrySet()) {
				if (!entry.getValue().className.equals(clazz.getName()))
					continue;
				arrayList.add(entry.getKey());
			}
			for (String string : arrayList) {
				onActionShift.remove(string);
			}
		} catch (Exception exception) {
			_log.error("", exception);
		}
	}

	public Object callScripts(String string, String string2) {
		return this.callScripts(null, string, string2, null, null);
	}

	public Object callScripts(String string, String string2, Object[] objectArray) {
		return this.callScripts(null, string, string2, objectArray, null);
	}

	public Object callScripts(String string, String string2, Map<String, Object> map) {
		return this.callScripts(null, string, string2, ArrayUtils.EMPTY_OBJECT_ARRAY, map);
	}

	public Object callScripts(String string, String string2, Object[] objectArray, Map<String, Object> map) {
		return this.callScripts(null, string, string2, objectArray, map);
	}

	public Object callScripts(Player player, String string, String string2) {
		return this.callScripts(player, string, string2, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
	}

	public Object callScripts(Player player, String string, String string2, Object[] objectArray) {
		return this.callScripts(player, string, string2, objectArray, null);
	}

	public Object callScripts(Player player, String string, String string2, Map<String, Object> map) {
		return this.callScripts(player, string, string2, ArrayUtils.EMPTY_OBJECT_ARRAY, map);
	}

	public Object callScripts(Player player, String string, String string2, Object[] objectArray,
			Map<String, Object> map) {
		Object object;
		Object obj;
		Class<?> clazz = this.aof.get(string);
		if (clazz == null) {
			_log.error("Script class " + string + " not found!");
			return null;
		}
		try {
			obj = instantiateScript(clazz);
		} catch (Exception exception) {
			_log.error("Scripts: Failed creating instance of " + clazz.getName(), exception);
			return null;
		}
		if (map != null && !map.isEmpty()) {
			for (Map.Entry object2 : map.entrySet()) {
				try {
					FieldUtils.writeField(obj, (String) object2.getKey(), object2.getValue());
				} catch (Exception i) {
					_log.error("Scripts: Failed setting fields for " + clazz.getName(), i);
				}
			}
		}
		if (player != null) {
			try {
				object = null;
				object = FieldUtils.getField(clazz, "self");
				if (object != null) {
					FieldUtils.writeField((Field) object, obj, player.getRef());
				}
			} catch (Exception exception) {
				_log.error("Scripts: Failed setting field for " + clazz.getName(), exception);
			}
		}
		object = null;
		try {
			Class[] noSuchMethodException = new Class[objectArray.length];
			for (int i = 0; i < objectArray.length; ++i) {
				noSuchMethodException[i] = objectArray[i] != null ? objectArray[i].getClass() : null;
			}
			object = MethodUtils.invokeMethod(obj, string2, objectArray, noSuchMethodException);
		} catch (NoSuchMethodException invocationTargetException) {
			_log.error("Scripts: No such method " + clazz.getName() + "." + string2 + "()!");
		} catch (InvocationTargetException exception) {
			_log.error("Scripts: Error while calling " + clazz.getName() + "." + string2 + "()",
					exception.getTargetException());
		} catch (Exception exception) {
			_log.error("Scripts: Failed calling " + clazz.getName() + "." + string2 + "()", exception);
		}
		return object;
	}

	public Map<String, Class<?>> getClasses() {
		return this.aof;
	}

	public static class ScriptClassAndMethod {
		public final String className;
		public final String methodName;

		public ScriptClassAndMethod(String string, String string2) {
			this.className = string;
			this.methodName = string2;
		}
	}
}
