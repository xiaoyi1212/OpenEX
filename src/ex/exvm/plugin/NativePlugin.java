package ex.exvm.plugin;

import ex.exvm.lib.NativeLib;

public abstract class NativePlugin implements NativeLib {
    public abstract void onLoad();
}
