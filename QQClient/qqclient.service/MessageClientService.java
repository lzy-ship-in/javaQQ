package com.hspedu.qqclient.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Date;

import com.hspedu.qqcommon.Message;
import com.hspedu.qqcommon.MessageType;

//该类/对象，提供和消息相关的服务方法
public class MessageClientService {
       public void sendMessageToOne(String content,String senderId,String getterId) {
    	   Message message=new Message();
    	   message.setMestype(MessageType.MESSAGE_COMM_MES);
    	   message.setSender(senderId);
    	   message.setGetter(getterId);
    	   message.setContent(content);
    	   message.setSendtime(new java.util.Date().toString());
           System.out.println(senderId+" 对 "+getterId+"说"+content);
           try {
        	   ObjectOutputStream oos=new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
               oos.writeObject(message);
           }catch(IOException e) {
        	   e.printStackTrace();
           }
       }
    // 群发消息方法
       public void sendMessageToAll(String content, String senderId) {
           Message message = new Message();
           message.setMestype(MessageType.MESSAGE_TO_ALL_MES);
           message.setSender(senderId);
           message.setContent(content);
           message.setSendtime(new java.util.Date().toString());
           
           try {
               ObjectOutputStream oos = new ObjectOutputStream(
                   ManageClientConnectServerThread.getClientConnectServerThread(senderId)
                       .getSocket().getOutputStream());
               oos.writeObject(message);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       // 发送文件方法
       public void sendFileToOne(String src, String dest, String senderId, String getterId) {
           Message message = new Message();
           message.setMestype(MessageType.MESSAGE_FILE_MES);
           message.setSender(senderId);
           message.setGetter(getterId);
           message.setFileSrc(src);
           message.setFileDest(dest);
           
           try {
               // 读取文件内容
               File file = new File(src);
               byte[] fileBytes = new byte[(int) file.length()];
               FileInputStream fis = new FileInputStream(file);
               fis.read(fileBytes);
               fis.close();
               
               message.setFileBytes(fileBytes);
               
               ObjectOutputStream oos = new ObjectOutputStream(
                   ManageClientConnectServerThread.getClientConnectServerThread(senderId)
                       .getSocket().getOutputStream());
               oos.writeObject(message);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
}
