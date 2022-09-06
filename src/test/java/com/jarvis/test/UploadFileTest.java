package com.jarvis.test;

import org.junit.jupiter.api.Test;

public class UploadFileTest {

    @Test
    public void testUploadFile() {
        String fileName = "qweqweqwe.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);

    }
}
