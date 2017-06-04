package com.example.yang.flashtable.customer.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.yang.flashtable.R;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by Yang on 2017/6/4.
 */

public class Sftp {
    Activity activity;
    JSch jSch;
    public Sftp(Activity activity){
        this.activity = activity;
        host = activity.getString(R.string.server_ip);
        username = activity.getString(R.string.server_acc);
        password = activity.getString(R.string.server_pwd);
        jSch = new JSch();
    }
    String host;
    final int port = 22;
    String username;
    String password;

    public Bitmap get(String filename){
        return getBitmap("FlashTable/public", filename);
    }

    public boolean put(Bitmap bitmap, String filename){
        return putBitmap(bitmap, "FlashTable/public", filename);
    }

    public Bitmap getBitmap(String path, String filename){
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        try {
            ChannelSftp sftp = setSftp();
            if(sftp == null)
                return null;
            System.out.println("Connected to " + host + ".");
            sftp.cd(path);
            sftp.get(filename, fout);
            byte[] download = fout.toByteArray();
            System.out.println("size = "+download.length);
            Bitmap bitmap = BitmapFactory.decodeByteArray(download, 0, download.length);
            sftp.exit();
            return bitmap;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean putBitmap(Bitmap bitmap, String path, String filename){
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
        byte[] bytes = fout.toByteArray();
        ByteArrayInputStream fin = new ByteArrayInputStream(bytes);
        ChannelSftp sftp = setSftp();
        if (sftp == null)
            return false;
        try {
            sftp.cd(path);
            sftp.put(fin, filename);
            sftp.exit();
        } catch (SftpException e) {
            e.printStackTrace();
        }


        return true;
    }

    private ChannelSftp setSftp(){
        ChannelSftp sftp = null;
        Session session = null;
        try {
            session = jSch.getSession(username, host, port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return sftp;
    }


}
