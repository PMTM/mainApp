package cz.xlinux.ifImpl;

import aidl.core.API.AbstractBestInterface;

public class MyFirstBestIfImpl extends AbstractBestInterface implements MyFirstBestIf {
    
    private final String verRepl;

    public MyFirstBestIfImpl(String version) {
        verRepl=version;
    }

    /* (non-Javadoc)
     * @see cz.xlinux.ifImpl.MyFirstBestIf#getVerRepl()
     */
    @Override
    public String getVerRepl() {
        return verRepl;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -8682597996162398585L;

    @Override
    public String getMWVersion() {
        return "v1.0";
    }

}
