package com.leoleozhu.utils;

import junit.framework.TestCase;

import java.io.File;

public class TestCaseBase extends TestCase {

    private File resourceFolder = new File("src/test/resources");
    private File targetFolder = new File("target/output");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (!resourceFolder.exists())
            resourceFolder.mkdirs();

        if (!targetFolder.exists())
            targetFolder.mkdirs();
    }

    @SuppressWarnings("unused")
    protected String resourceFile(String name) {
        return new File(resourceFolder, name).getAbsolutePath();
    }

    @SuppressWarnings("unused")
    protected String targetFile(String name) {
        return new File(targetFolder, name).getAbsolutePath();
    }


    @SuppressWarnings("unused")
    protected static float mm2pt(float mm) {
        return inch2pt(mm2inch(mm));
    }

    @SuppressWarnings("unused")
    protected static float pt2mm(float pt) {
        return inch2mm(pt2inch(pt));
    }

    @SuppressWarnings("unused")
    protected static float inch2pt(float inch) {
        return inch * 72f;
    }

    @SuppressWarnings("unused")
    protected static float pt2inch(float pt) { return pt / 72f; }

    @SuppressWarnings("unused")
    protected static float inch2mm(float inch) { return inch * 25.4f; }

    @SuppressWarnings("unused")
    protected static float mm2inch(float mm) { return mm / 25.4f; }
}
