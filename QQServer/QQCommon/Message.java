package com.hspedu.qqcommon;

import java.io.Serializable;
//表示客户端和服务端通信时的消息对象
public class Message implements Serializable{
	 private static final long serialVersionUID=1L;
	 private String sender;//发送者
	 private String getter;//接受者
	 private String content;//消息内容
	 private String sendtime;//发送时间
	 private String mesType;//可以在接口定义消息类型
	 private byte[] fileBytes;  // 文件字节数组
	 private String fileSrc;    // 源文件路径
	 private String fileDest;   // 目标文件路径
	
	 public void setSender(String sender) {
  	 this.sender=sender;
   }
	 public void setGetter(String getter) {
  	 this.getter=getter;
   }
	 public void setContent(String content) {
  	 this.content=content;
   }
	 public void setSendtime(String sendTime) {
  	 this.sendtime=sendTime;
   }
	 public String getSender() {
		 return sender;
	 }
	 public String getGetter() {
		 return getter;
	 }
	 public String getContent() {
		 return content;
	 }
	 public String getSendtime() {
		 return sendtime;
	 }
	 public void setMestype(String mestype) {
		 this.mesType=mestype;
	 }
	 public String getMestype() {
		 return mesType;
	 }
	// 新增文件相关方法
	    public byte[] getFileBytes() {
	        return fileBytes;
	    }

	    public void setFileBytes(byte[] fileBytes) {
	        this.fileBytes = fileBytes;
	    }

	    public String getFileSrc() {
	        return fileSrc;
	    }

	    public void setFileSrc(String fileSrc) {
	        this.fileSrc = fileSrc;
	    }

	    public String getFileDest() {
	        return fileDest;
	    }

	    public void setFileDest(String fileDest) {
	        this.fileDest = fileDest;
	    }
	}
