package com.angusj.clipper;

import java.nio.file.FileSystems;


public class Clipper {
    static {
        String folder;
        String ending;

        if(System.getProperty("os.name").startsWith("Mac OS X")){
            folder = "macos";
            ending = ".dylib";
        }else if (System.getProperty("os.name").startsWith("Windows")) {
            ending = ".dll";
            folder = "windows";
        } else {
            ending =".so";
            folder = "linux";
        }

        try{
            NativeUtils.loadLibraryFromJar("/" + folder + "/libclipper-native" + ending);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void loadLibrary(String path){
        System.load(
                FileSystems.getDefault()
                        .getPath(path)  // Static link
                        .normalize().toAbsolutePath().toString());
    }

    public native double[][] generatePath(double[][] values, double delta);
}
