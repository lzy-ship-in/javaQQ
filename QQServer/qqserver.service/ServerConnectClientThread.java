package com.hspedu.qqserver.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.hspedu.qqcommon.Message;
import com.hspedu.qqcommon.MessageType;

//该类的一个对象和某个客户端保持通信
public class ServerConnectClientThread extends Thread{
       private Socket socket;
       private String userId;//连接到服务端的用户id
       public ServerConnectClientThread(Socket socket,String userId) {
    	   this.socket=socket;
    	   this.userId=userId;
       }
       public Socket getsocket() {
    	   return socket;
       }
       public void run() {
    	   //这里线程处于run的状态，可以接受\发送信息
    	   while(true) {
    		   try {
    			   System.out.println("服务端和客户端"+userId+"保持通信");
        		   ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
    		       Message message=(Message)ois.readObject();
    		   //后面会使用message，根据message的类型做相应的业务处理
    		       if(message.getMestype().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
    		    	   //客户端要在线用户列表
    		    	   //在线用户列表形式：100 200 300
    		    	   System.out.println(message.getSender()+"要在线用户列表");
    		    	   String onlineUser=ManageClientThreads.getOnlineUser();
    		           //返回
    		    	   //构建一个message对象，返回给客户端
    		    	   Message message2=new Message();
    		    	   message2.setMestype(MessageType.MESSAGE_RET_ONLINE_FRIEND);
    		           message2.setContent(onlineUser);
    		           message2.setGetter(message.getSender());
    		          //返回到客户端
    		           ObjectOutputStream oos=  new ObjectOutputStream(socket.getOutputStream());
    		           oos.writeObject(message2);
    		         
    		       }else if(message.getMestype().equals(MessageType.MESSAGE_COMM_MES)) {
    		    	   //根据message获取getter id,然后再得到对应线程
    		    	   ServerConnectClientThread serverConnectClientThread=ManageClientThreads.getServerConnectClientThread(message.getGetter());
    		    	   ObjectOutputStream oos= new ObjectOutputStream(serverConnectClientThread.getsocket().getOutputStream());
    		           oos.writeObject(message);
    		       }
    		       else if(message.getMestype().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
    		    	   System.out.println(message.getSender()+" 退出 ");
    		    	   //将这个客户端对应线程从客户端移除
    		    	   ManageClientThreads.removeServerConnectClientThread(message.getSender());
    		           socket.close();//关闭连接
    		           //退出线程
    		           break;
    		       }else if(message.getMestype().equals(MessageType.MESSAGE_TO_ALL_MES)) {
    		    	    // 群发消息处理
    		    	    String onlineUser = ManageClientThreads.getOnlineUser();
    		    	    String[] users = onlineUser.split(" ");
    		    	    for(String user : users) {
    		    	        if(!user.equals(message.getSender())) {
    		    	            ServerConnectClientThread thread = ManageClientThreads.getServerConnectClientThread(user);
    		    	            ObjectOutputStream oos = new ObjectOutputStream(thread.getsocket().getOutputStream());
    		    	            oos.writeObject(message);
    		    	        }
    		    	    }
    		    	} else if(message.getMestype().equals(MessageType.MESSAGE_FILE_MES)) {
    		    	    // 文件消息处理
    		    	    ServerConnectClientThread thread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
    		    	    ObjectOutputStream oos = new ObjectOutputStream(thread.getsocket().getOutputStream());
    		    	    oos.writeObject(message);
    		    	}
    		       
    		       else {
    		    	   System.out.println("其他类型的message暂时不处理");
    		       }
    		   }
    		   catch(Exception e){
    			   e.printStackTrace();
    		   }
    		   
    	   }
       }
}

