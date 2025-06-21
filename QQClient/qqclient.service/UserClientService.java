package com.hspedu.qqclient.service;

import com.hspedu.qqcommon.Message;
import com.hspedu.qqcommon.MessageType;
import com.hspedu.qqcommon.User;
import com.hspedu.qqclient.view.QQView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class UserClientService {
    private User u = new User();
    private Socket socket;
    private QQView qqView; 

    
    public boolean checkUser(String userId, String pwd, QQView view) {
        this.qqView = view; 
        boolean b = false;
        
        u.setUserId(userId);
        u.setPasswd(pwd);
        
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);
            
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();
            
            if (ms.getMestype().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {
                b = true;
                
               
                ClientConnectServerThread ccst = new ClientConnectServerThread(socket, qqView);
                ccst.start();
                
                ManageClientConnectServerThread.addClientConnectServerThread(userId, ccst);
            } else {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
    
    public void onlineFriendList() {
        Message message = new Message();
        message.setMestype(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId())
                    .getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void logout() {
        Message message = new Message();
        message.setMestype(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());
        
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId())
                    .getSocket().getOutputStream());
            oos.writeObject(message);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
