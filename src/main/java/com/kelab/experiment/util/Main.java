package com.kelab.experiment.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File path = new File("/tmp1586590044517");
        FileUtils.deleteDirectory(path);
    }
}
