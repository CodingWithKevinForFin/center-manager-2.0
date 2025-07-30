package com.sjls.f1.start.ofr.controlpanel;

import java.util.List;

import com.google.protobuf.Message;

public interface IBlockOrdersSnapshotProvider {

    public List<Message> getSnapShot();

}
