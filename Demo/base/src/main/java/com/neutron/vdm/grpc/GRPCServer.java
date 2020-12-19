package com.neutron.vdm.grpc;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.neutron.vdm.base.services.PeerService;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;

public class GRPCServer extends Service {
    public static final int SERVER_PORT = 8989;
    public static final String TAG = "CGM-GRPCServer";
    private Server mGrpcServer;

    public class GRPCBinder extends Binder {
        public GRPCServer getServer() {
            return GRPCServer.this;
        }
    }

    private GRPCBinder binder = new GRPCBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //keep alive
    @TargetApi(Build.VERSION_CODES.O)
    private void setServiceForeground(){
        NotificationChannel channel = new NotificationChannel(this.getPackageName(), this.getPackageName(),
                NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        Notification notification = new Notification
                .Builder(getApplicationContext(),this.getPackageName())
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentTitle("gRPC-Server Ready")
                .setContentText("提供跨端调用能力")
                .build();
        startForeground(2, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");
        // 解决Didn't find class "org.apache.logging.log4j.spi.ExtendedLoggerWrapper"
        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setServiceForeground();

        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(SERVER_PORT);
        serverBuilder.addService(new PeerService());

        mGrpcServer = serverBuilder.build();

        try {
            mGrpcServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
        mGrpcServer.shutdown();
        mGrpcServer = null;
    }
}
