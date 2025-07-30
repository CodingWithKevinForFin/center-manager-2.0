package com.f1.utils.ack;

import org.junit.Assert;
import java.io.File;
import java.io.IOException;
import org.junit.Test;

public class AckPersisterTest {

    @Test
    public void TestAckPersisterCtor() throws IOException {

        File file = new File("./test.txt");
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 100, false);

        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();
        }
    }

    @Test
    public void TestAckPersisterCtor2() throws IOException {

        File file = new File("");
        try {
            file.createNewFile();
            new AckPersister(file, 100, false);
            Assert.fail("Expected exception to be thrown, invalid constructor");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        } finally {
            file.delete();
        }
    }

    @Test
    public void TestAckPersisterCtor3() throws IOException {
        // Test for existing file
        File file = new File("./test.txt");
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 100, false);
            // Test for valid construction on an existing file
            new AckPersister(file, 100, false);

        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();
        }
    }

    @Test
    public void TestAckWriteMessage() throws IOException {
        File file = new File("./test.txt");
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 100, false);
            int result = ackPersister.writeMessage("Hello World!".getBytes());
            Assert.assertTrue(result >= 1);
        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();

        }
    }

    @Test
    public void TestWritePreparedMessage() throws IOException {
        File file = new File("./test.txt");
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 100, false);
            int result = ackPersister.writePreparedMessageAndStoreAckId("\u0000ff00".getBytes());
            Assert.assertTrue(result >= 1);
        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();

        }
    }

    @Test
    public void TestWritePreparedMessage2() throws IOException {
        File file = new File("./test.txt");
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 100, true);
            ackPersister.writePreparedMessageAndStoreAckId("\u000000".getBytes());
            Assert.fail("Expected safety check to fail with message [" + "\u000000" + "]");
        } catch (Exception e) {
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();

        }
    }

    @Test
    public void TestWritePreparedMessage3() throws IOException {
        File file = new File("./test.txt");
        byte[] b = new byte[5];
        for (int i = 0; i < 5; ++i)
            b[i] = (byte) 0;
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 100, true);
            ackPersister.writePreparedMessageAndStoreAckId(b);
            Assert.fail("Expected safety check to fail, invalid message format: with message [" + b.toString() + "]");
        } catch (Exception e) {
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();
        }
    }

    @Test
    public void TestWritePreparedMessage4() throws IOException {
        File file = new File("./test.txt");
        byte[] b = new byte[9];
        for (int i = 0; i < 9; ++i)
            b[i] = (byte) 0;
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 100, true);
            ackPersister.writePreparedMessageAndStoreAckId(b);
            Assert.fail("Expected safety check to fail, invalid message size: with message [" + b.toString() + "]");
        } catch (Exception e) {
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();
        }
    }

    @Test
    public void TestWritePreparedMessage5() throws IOException {
        File file = new File("./test.txt");
        byte[] b = new byte[9];
        for (int i = 0; i < 9; ++i)
            b[i] = (byte) 0;
        b[7] = 1;
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 9, true);
            ackPersister.writePreparedMessageAndStoreAckId(b);
        } catch (Exception e) {
            Assert.fail("Exception: " + e);
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();
        }
    }

    @Test
    public void TestReadMessage() throws IOException {
        File file = new File("./test.txt");
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 10000, true);
            byte[] message = "Hello World!".getBytes();
            int result = ackPersister.writeMessage(message);
            byte[] received = ackPersister.readMessage(result);
            for (int i = 0; i < message.length; ++i) {
                Assert.assertEquals(message[i], received[i + 8]);
            }

        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();

        }
    }

    @Test
    public void TestReadMessage2() throws IOException {
        File file = new File("./test.txt");
        AckPersister ackPersister = null;
        try {
            file.createNewFile();
            ackPersister = new AckPersister(file, 10000, true);
            int result = ackPersister.writeMessage("Hello World!".getBytes());
            Assert.assertNull(ackPersister.readMessage(result + 1));
        } catch (Exception e) {
            Assert.fail(e.toString());
        } finally {
            if (ackPersister != null)
                ackPersister.close();
            file.delete();

        }
    }

}
