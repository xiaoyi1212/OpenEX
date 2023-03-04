package ex.exvm.lib;

import java.util.ArrayList;

public interface NativeLib {
    public ArrayList<NativeFunction> getFunctions();
    public String getName();
}
