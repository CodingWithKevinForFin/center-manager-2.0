package com.f1.utils.ack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.f1.utils.AbstractAckable;

public class FileAckerReaderTest {

    private static final String filename = "./FileAckerReaderTest.txt";

    @Test
    public void TestFileAckerReaderCtor() throws IOException {
        File file = new File(filename);
        FileAcker f = null;
        try {
            f = new FileAcker(file, 100);
            f.ack(1, 1, 2, 3, 4, 56, 7);
        } finally {
            if (f != null)
                f.close();
        }
        new FileAckerReader(file);
        file.delete();
    }

    @Test(expected = IOException.class)
    public void TestFileAckerReaderCtor2() throws IOException {
        File file = new File(filename);
        file.createNewFile();
        Files.write(Paths.get(file.getPath()), Arrays.asList("Hello World"), StandardCharsets.UTF_8);
        try {
            new FileAckerReader(file);
        } finally {
            file.delete();
        }
    }

}
