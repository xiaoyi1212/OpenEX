package ex.openex;

import ex.openex.exception.VMException;

public class VMOutputStream {

    public VMOutputStream() throws VMException {

    }


    public void info(String str) {
        System.out.println(str);
    }

    public void error(String str) {
        System.err.println(str);
    }
}