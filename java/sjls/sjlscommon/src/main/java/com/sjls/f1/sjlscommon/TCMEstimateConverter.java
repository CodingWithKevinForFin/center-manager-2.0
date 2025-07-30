package com.sjls.f1.sjlscommon;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.utils.converter.bytes.AbstractCustomByteArrayConverter;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;
import com.sjls.algos.eo.common.ITCMEstimate;

public class TCMEstimateConverter extends AbstractCustomByteArrayConverter<ITCMEstimate> {

    public TCMEstimateConverter() {
        super(ITCMEstimate.class, ITCMEstimate.class.getSimpleName());
    }

    @Override
    public void write(ITCMEstimate o, ToByteArrayConverterSession session) throws IOException {
        DataOutput stream = session.getStream();
        stream.writeDouble(o.getTotalCost());
        stream.writeDouble(o.getAlhpaCost());
        stream.writeDouble(o.getMktImpactCost());
        stream.writeDouble(o.getSpreadCost());
        stream.writeDouble(o.getStdDevOfTotalCost());
    }

    @Override
    public ITCMEstimate read(FromByteArrayConverterSession session) throws IOException {
        DataInput i = session.getStream();
        return new SimpleTCMEstimate(i.readDouble(), i.readDouble(), i.readDouble(), i.readDouble(), i.readDouble());
    }

    public static class SimpleTCMEstimate implements ITCMEstimate {

        final private double totalCost, alhpaCost, mktImpactCost, spreadCost, stdDevOfTotalCost;

        public SimpleTCMEstimate(double totalCost, double alhpaCost, double mktImpactCost, double spreadCost, double stdDevOfTotalCost) {
            super();
            this.totalCost = totalCost;
            this.alhpaCost = alhpaCost;
            this.mktImpactCost = mktImpactCost;
            this.spreadCost = spreadCost;
            this.stdDevOfTotalCost = stdDevOfTotalCost;
        }

        @Override
        public double getTotalCost() {
            return totalCost;
        }

        @Override
        public double getAlhpaCost() {
            return alhpaCost;
        }

        @Override
        public double getMktImpactCost() {
            return mktImpactCost;
        }

        @Override
        public double getSpreadCost() {
            return spreadCost;
        }

        @Override
        public double getStdDevOfTotalCost() {
            return stdDevOfTotalCost;
        }

    }
}
