package ex.exvm.plugin;

import java.net.URLClassLoader;

public class Plugin {
    String name;
    URLClassLoader loader;
    NativePlugin main;
    public Plugin(NativePlugin i, String name, URLClassLoader classLoader) {
        this.main = i;
        this.name = name;
        this.loader = classLoader;
    }

    public NativePlugin getMain() {
        return main;
    }
}
