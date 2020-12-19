package com.neutron.vdm.demo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetUtils {
    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetInfo == null)
            return false;

        return (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    public static byte[] getIpBytesAddress(Context context) {
        int ip = getLocalIpAddress(context);

        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((ip >> 24) & 0xff);
        bytes[1] = (byte) ((ip >> 16) & 0xff);
        bytes[2] = (byte) ((ip >> 8) & 0xff);
        bytes[3] = (byte) (ip & 0xff);

        return bytes;
    }

    public static String getIpStringAddress(Context context) {
        int ip = getLocalIpAddress(context);

        return int2ip(ip);
    }

    public static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xff) << 24
                | (bytes[1] & 0xff) << 16
                | (bytes[2] & 0xff) << 8
                | (bytes[3] & 0xff);
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    public static int getLocalIpAddress(Context context) {
        try {

            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getIpAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}
