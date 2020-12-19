package com.neutron.vdm.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neutron.vdm.BasicDeviceInfo;
import com.neutron.vdm.base.clients.PeerClient;
import com.neutron.vdm.demo.utils.NetUtils;
import com.neutron.vdm.grpc.GRPCServer;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "CGM";
    private final String REMOTE_ADDR_KEY = "REMOTE_ADDR";

    private GRPCServer mServer = null;
    private TextView mStatus;
    private Button mServerCtrlBtn;
    private String mRemoteAddr;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DeviceAdapter mAdapter;
    private SharedPreferences mSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatus = findViewById(R.id.status);
        mServerCtrlBtn = findViewById(R.id.grpc_server);
        mRecyclerView = findViewById(R.id.device_list);
        EditText evAddr = findViewById(R.id.remote_addr);
        mSP = getPreferences(MODE_PRIVATE);
        mRemoteAddr = mSP.getString(REMOTE_ADDR_KEY, "localhost");
        evAddr.setText(mRemoteAddr);

        evAddr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(MainActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                mRemoteAddr = text;
                mSP.edit().putString(REMOTE_ADDR_KEY, text).apply();
                Log.d(TAG, "got " + mRemoteAddr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mAdapter = new DeviceAdapter();
        mRecyclerView.setAdapter(mAdapter);

        TextView ipAddr = findViewById(R.id.ipAddr);
        ipAddr.setText("本机: " + NetUtils.getIpStringAddress(this));
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            GRPCServer.GRPCBinder grpcBinder = (GRPCServer.GRPCBinder)binder;
            mServer = grpcBinder.getServer();

            mStatus.setText("已发布");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServer = null;

            mStatus.setText("已停止");
        }
    };

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.grpc_server:
                if (mServer == null) {
                    Intent grpc = new Intent(this, GRPCServer.class);
                    bindService(grpc, conn, BIND_AUTO_CREATE);

                    mServerCtrlBtn.setText("停止本机服务");
                } else {
                    unbindService(conn);
                    mServer = null;
                    mStatus.setText("已停止");
                    mServerCtrlBtn.setText("发布本机服务");
                }

                break;

            case R.id.get_devices:

                PeerClient client = new PeerClient(mRemoteAddr);
                List<BasicDeviceInfo> devices = client.getDevices();
                if (devices == null) {
                    Toast.makeText(this, "获取失败，请检查远端服务状态", Toast.LENGTH_SHORT).show();
                }

                mAdapter.setData(devices);
                mAdapter.notifyDataSetChanged();

                break;
        }
    }
}