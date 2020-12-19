package com.neutron.vdm.base.services;

import com.neutron.vdm.BaseVirtualDevice;
import com.neutron.vdm.BasicDeviceInfo;
import com.neutron.vdm.DeviceType;
import com.neutron.vdm.PeerGrpc;
import com.neutron.vdm.VirtualDeviceListReply;
import com.neutron.vdm.VirtualDeviceListRequest;

import java.util.ArrayList;
import java.util.List;

import io.grpc.stub.StreamObserver;

public class PeerService extends PeerGrpc.PeerImplBase {
    private List<BasicDeviceInfo> devices = new ArrayList<>();

    public PeerService() {
        BasicDeviceInfo.Builder speaker = BasicDeviceInfo.newBuilder();
        speaker.setType(DeviceType.AUDIO_OUT);
        speaker.setName("rSpeaker");
        BasicDeviceInfo.Builder camera = BasicDeviceInfo.newBuilder();
        camera.setType(DeviceType.VIDEO_IN);
        camera.setName("rCamera");

        devices.add(speaker.build());
        devices.add(camera.build());
    }

    @Override
    public void getDevices(VirtualDeviceListRequest request, StreamObserver<VirtualDeviceListReply> responseObserver) {
        VirtualDeviceListReply.Builder builder = VirtualDeviceListReply.newBuilder();

        for (BasicDeviceInfo device : devices) {
            builder.addDevices(device);
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
