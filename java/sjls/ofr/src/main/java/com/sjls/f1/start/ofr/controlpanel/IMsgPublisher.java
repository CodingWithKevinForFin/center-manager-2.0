package com.sjls.f1.start.ofr.controlpanel;

import java.util.List;

import com.google.protobuf.Message;

public interface IMsgPublisher {
    void publish(List<Message> list);
    void publish(Message msg);
}
