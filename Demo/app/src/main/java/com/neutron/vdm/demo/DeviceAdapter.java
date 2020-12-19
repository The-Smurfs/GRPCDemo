package com.neutron.vdm.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neutron.vdm.BasicDeviceInfo;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<BasicDeviceInfo> mDevices;

    public void setData(List<BasicDeviceInfo> devices) {
        mDevices = devices;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        DeviceViewHolder vh = new DeviceViewHolder(root);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BasicDeviceInfo dev = mDevices.get(position);

        holder.tvName.setText(dev.getName());
        holder.tvType.setText(dev.getType() + "");
    }

    @Override
    public int getItemCount() {
        return mDevices == null ? 0 : mDevices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvType;
        public DeviceViewHolder(View root) {
            super(root);
            tvName = root.findViewById(R.id.device_name);
            tvType = root.findViewById(R.id.device_type);
        }
    }
}
