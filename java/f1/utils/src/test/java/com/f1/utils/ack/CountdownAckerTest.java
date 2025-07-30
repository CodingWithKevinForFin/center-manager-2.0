package com.f1.utils.ack;

import org.junit.Assert;
import java.util.ArrayList;

import org.junit.Test;

import com.f1.base.Ackable;
import com.f1.utils.AbstractAckable;
import com.f1.utils.AssertionException;
import com.f1.utils.structs.Tuple2;

public class CountdownAckerTest {

    @Test
    public void TestCountdownAckerCtor() {
        CountdownAcker acker = new CountdownAcker(10);
        Assert.assertNotNull(acker);
    }

    @Test(expected = AssertionException.class)
    public void TestCountdownAckerCtor2() {
        new CountdownAcker(0);
    }

    @Test(expected = AssertionException.class)
    public void TestCountdownAckerCtor3() {
        new CountdownAcker(-10);
    }

    @Test
    public void TestCountdownAckerAck() {
        CountdownAcker acker = new CountdownAcker(10);
        acker.ack(new AbstractAckable(), null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void TestCountdownAckerAck2() {
        CountdownAcker acker = new CountdownAcker(1);
        acker.ack(new AbstractAckable(), null);
        acker.ack(new AbstractAckable(), null);
    }

    @Test
    public void TestCountdownAckerGetAcks() {
        CountdownAcker acker = new CountdownAcker(2);
        ArrayList<Tuple2<Ackable, Object>> list = new ArrayList<Tuple2<Ackable, Object>>();
        list.add(new Tuple2(new AbstractAckable(), null));
        list.add(new Tuple2(new AbstractAckable(), null));
        for (final Tuple2<Ackable, Object> t : list)
            acker.ack(t.getA(), t.getB());

        Assert.assertEquals(list, acker.getAcks());
    }

    @Test
    public void TestCountdownAckerGetAckedCount() {
        CountdownAcker acker = new CountdownAcker(2);
        acker.ack(new AbstractAckable(), null);
        Assert.assertEquals(1, acker.getAckedCount());
    }

    @Test
    public void TestCountdownAckerGetUnackedCount() {
        CountdownAcker acker = new CountdownAcker(2);
        acker.ack(new AbstractAckable(), null);
        Assert.assertEquals(1, acker.getUnackedCount());
    }

    @Test
    public void TestCountdownAckerWaitForAcks() {
        CountdownAcker acker = new CountdownAcker(1);
        acker.ack(new AbstractAckable(), null);
        Assert.assertEquals(true, acker.waitForAcks(10));
    }

    @Test
    public void TestCountdownAckerWaitForAcks2() {
        CountdownAcker acker = new CountdownAcker(1);
        Assert.assertEquals(false, acker.waitForAcks(10));
    }

    @Test(expected = AssertionException.class)
    public void TestCountdownAckerWaitForAcks3() {
        CountdownAcker acker = new CountdownAcker(1);
        acker.ack(new AbstractAckable(), null);
        acker.waitForAcks(0);
    }

    private class AckMessage implements Runnable {

        CountdownAcker acker;

        AckMessage(final CountdownAcker acker) {
            this.acker = acker;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }

            System.out.println("Hello");
            acker.ack(new AbstractAckable(), null);

        }
    }

    @Test
    public void TestCountdownAckerWaitForAcks4() {
        CountdownAcker acker = new CountdownAcker(1);
        Thread t = new Thread(new AckMessage(acker));
        t.start();
        Assert.assertEquals(true, acker.waitForAcks(2000));
    }

}
