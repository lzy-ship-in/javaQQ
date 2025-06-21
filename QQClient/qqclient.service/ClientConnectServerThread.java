package com.hspedu.qqclient.service;

import com.hspedu.qqcommon.Message;
import com.hspedu.qqcommon.MessageType;
import com.hspedu.qqclient.view.QQView;

import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread {
    private Socket socket;
    private QQView qqView; 

   
    public ClientConnectServerThread(Socket socket, QQView view) {
        this.socket = socket;
        this.qqView = view;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public void run() {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                
                if (message.getMestype().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                   //使用UI方法更新列表
                    String[] onlineUsers = message.getContent().split(" ");
                    qqView.updateUserList(onlineUsers);
                } else if (message.getMestype().equals(MessageType.MESSAGE_COMM_MES)) {
                    // 使用UI方法显示消息
                    String msg = "\n" + message.getSender() + " 对你说: " + message.getContent();
                    qqView.showMessage(msg);
                } else if (message.getMestype().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    // 使用UI方法显示群消息
                    String msg = "\n" + message.getSender() + " 对大家说: " + message.getContent();
                    qqView.showMessage(msg);
                } else if (message.getMestype().equals(MessageType.MESSAGE_FILE_MES)) {
                    // 使用UI方法显示文件消息
                    String msg = "\n收到来自 " + message.getSender() + " 的文件: " + message.getFileDest();
                    qqView.showMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
