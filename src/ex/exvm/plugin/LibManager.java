package ex.exvm.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LibManager {
    static ArrayList<Plugin> plugins = new ArrayList<>();

    public static ArrayList<Plugin> getPlugins() {
        return plugins;
    }

    public static void loadLib(File file) throws Exception {
        if (!file.exists()) throw new FileNotFoundException("找不到指定库文件");

        JarFile jar = new JarFile(file);
        JarEntry entry = jar.getJarEntry("config.properties");
        Properties config = new Properties();
        config.load(jar.getInputStream(entry));

        URL url = file.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
        Class<NativePlugin> c = (Class<NativePlugin>) classLoader.loadClass(config.getProperty("class"));
        NativePlugin i = c.newInstance();
        plugins.add(new Plugin(i, config.getProperty("name"), classLoader));
    }

    public static void loadPlugin(){
        for(Plugin plugin:plugins){
            plugin.main.onLoad();
        }
    }
}
