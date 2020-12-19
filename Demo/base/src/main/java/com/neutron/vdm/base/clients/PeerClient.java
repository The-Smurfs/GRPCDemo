package com.neutron.vdm.base.clients;

import android.text.TextUtils;

import com.neutron.vdm.BasicDeviceInfo;
import com.neutron.vdm.DeviceType;
import com.neutron.vdm.PeerGrpc;
import com.neutron.vdm.VirtualDeviceListReply;
import com.neutron.vdm.VirtualDeviceListRequest;

import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static com.neutron.vdm.grpc.GRPCServer.SERVER_PORT;

public class PeerClient {
    private String mAddr;

    public PeerClient(String addr) {
        mAddr = TextUtils.isEmpty(addr) ? "localhost" : addr;
    }

    public List<BasicDeviceInfo> getDevices() {
        ManagedChannel mcb = null;
        try {
            mcb = ManagedChannelBuilder.forAddress(mAddr, SERVER_PORT).usePlaintext().build();
            PeerGrpc.PeerBlockingStub stub = PeerGrpc.newBlockingStub(mcb);
            VirtualDeviceListRequest request = VirtualDeviceListRequest.newBuilder().setType(DeviceType.UNKNOWN).build();
            VirtualDeviceListReply reply = stub.getDevices(request);
            return reply.getDevicesList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mcb != null)
                mcb.shutdown();
        }

        return null;
    }
}