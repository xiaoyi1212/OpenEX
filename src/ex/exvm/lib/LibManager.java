package ex.exvm.lib;

import ex.exvm.plugin.Plugin;

import java.util.ArrayList;

public class LibManager {
    ArrayList<NativeLib> libs;
    private LibManager(){}
    public static LibManager loadLibrary(){
        LibManager l = new LibManager();
        l.libs = new ArrayList<>();
        l.libs.add(new Sys());
        l.libs.add(new Util());
        l.libs.add(new Math());

        for(Plugin plugin: ex.exvm.plugin.LibManager.getPlugins())l.libs.add(plugin.getMain());
        return l;
    }

    public ArrayList<NativeLib> getLibs() {
        return libs;
    }
}
