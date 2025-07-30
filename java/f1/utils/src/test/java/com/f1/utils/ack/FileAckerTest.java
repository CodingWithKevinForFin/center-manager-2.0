package com.f1.utils.ack;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.f1.utils.AbstractAckable;

public class FileAckerTest {

    private static final String filename = "./FileAckerTest.txt";

    @Test
    public void TestFileAckerCtor() throws IOException {
        File file = new File(filename);
        FileAcker f = null;
        try {
            f = new FileAcker(file, 100);
        } finally {
            if (f != null)
                f.close();
            file.delete();
        }
    }

    @Test
    public void TestFileAckerCtor2() throws IOException {
        File file = new File(filename);
        file.createNewFile();

        FileAcker f = null;
        try {
            f = new FileAcker(file, 100);
        } finally {
            if (f != null)
                f.close();
            file.delete();
        }
    }

    // @Test(expected = IOException.class)
    // public void TestFileAckerCtor3() throws IOException {
    // File file = new File("/");
    // FileAcker f = null;
    // try {
    // f = new FileAcker(file, 100);
    // } finally {
    // if (f != null)
    // f.close();
    // }
    // }

    @Test
    public void TestFileAckerAck() throws IOException {
        File file = new File(filename);
        FileAcker f = null;
        try {
            f = new FileAcker(file, 100);
            f.ack(1);
        } finally {
            if (f != null)
                f.close();
            file.delete();
        }
    }

    @Test(expected = RuntimeException.class)
    public void TestFileAckerAck2() throws IOException {
        File file = new File(filename);
        FileAcker f = null;
        try {
            f = new FileAcker(file, 100);
            f.ack(0);
        } finally {
            if (f != null)
                f.close();
            file.delete();
        }
    }

    @Test
    public void TestFileAckerAck3() throws IOException {
        File file = new File(filename);
        FileAcker f = null;
        try {
            f = new FileAcker(file, 100);
            f.ack(1, 1, 2, 3, 4, 56, 7);
        } finally {
            if (f != null)
                f.close();
            file.delete();
        }
    }

    // Total size does nothing?
    @Test
    public void TestFileAckerAck4() throws IOException {
        File file = new File(filename);
        FileAcker f = null;
        try {
            f = new FileAcker(file, 4);
            f.ack(1, 2, 3, 4, 5, 6, 7, 8, 9);
        } finally {
            if (f != null)
                f.close();
            file.delete();
        }
    }

    @Test
    public void TestFileAckerAck5() throws IOException {
        File file = new File(filename);
        FileAcker f = null;
        try {
            f = new FileAcker(file, 4);
            f.ack(new AbstractAckable(), null);
            AbstractAckable ack = new AbstractAckable();
            ack.putAckId(20, false);
            f.ack(ack, null);
        } finally {
            if (f != null)
                f.close();
            file.delete();
        }
    }

}
