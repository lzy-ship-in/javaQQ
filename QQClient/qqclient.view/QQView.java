package com.hspedu.qqclient.view;

import com.hspedu.qqclient.service.ManageClientConnectServerThread;
import com.hspedu.qqclient.service.MessageClientService;
import com.hspedu.qqclient.service.UserClientService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class QQView extends JFrame {
    private UserClientService userClientService = new UserClientService();
    private MessageClientService messageClientService = new MessageClientService();
    private String currentUserId;
    
    // UI组件
    private JTextArea chatArea;
    private JTextField inputField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    
    public QQView() {
        initLoginUI();
    }
    
    private void initLoginUI() {
        setTitle("QQ登录");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel userLabel = new JLabel("用户ID:");
        JTextField userField = new JTextField();
        JLabel pwdLabel = new JLabel("密码:");
        JPasswordField pwdField = new JPasswordField();
        JButton loginBtn = new JButton("登录");
        
        panel.add(userLabel);
        panel.add(userField);
        panel.add(pwdLabel);
        panel.add(pwdField);
        panel.add(new JLabel());
        panel.add(loginBtn);
        
        // 传递当前UI引用给UserClientService
        loginBtn.addActionListener(e -> {
            String userId = userField.getText();
            String pwd = new String(pwdField.getPassword());
            
            // 将this(当前UI对象)传递给checkUser方法
            if (userClientService.checkUser(userId, pwd, this)) {
                currentUserId = userId;
                initMainUI();
            } else {
                JOptionPane.showMessageDialog(this, "登录失败");
            }
        });
        
        add(panel);
        setVisible(true);
    }
    
    private void initMainUI() {
        getContentPane().removeAll();
        setTitle("QQ聊天室 - " + currentUserId);
        setSize(800, 600);
        
        // 主布局
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(200);
        
        // 左侧用户列表
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        
        // 右侧聊天区域
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendBtn = new JButton("发送");
        JButton fileBtn = new JButton("发送文件");
        JButton groupBtn = new JButton("群发");
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 3));
        btnPanel.add(sendBtn);
        btnPanel.add(fileBtn);
        btnPanel.add(groupBtn);
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(btnPanel, BorderLayout.EAST);
        
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(userScrollPane);
        splitPane.setRightComponent(chatPanel);
        
        add(splitPane);
        
        // 事件监听
        sendBtn.addActionListener(e -> sendPrivateMessage());
        groupBtn.addActionListener(e -> sendGroupMessage());
        fileBtn.addActionListener(e -> sendFile());
        
        // 更新用户列表
        updateUserList();
        
        revalidate();
        repaint();
    }
    
    // 提供更新用户列表的方法
    public void updateUserList() {
        userClientService.onlineFriendList();
    }
    
    // 添加显示消息的方法
    public void showMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
        });
    }
    
    // 添加更新用户列表的方法
    public void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                if (!user.equals(currentUserId)) {
                    userListModel.addElement(user);
                }
            }
        });
    }
    
    private void sendPrivateMessage() {
        String content = inputField.getText();
        String receiver = userList.getSelectedValue();
        
        if (receiver != null && !content.isEmpty()) {
            messageClientService.sendMessageToOne(content, currentUserId, receiver);
            chatArea.append("你对 " + receiver + " 说: " + content + "\n");
            inputField.setText("");
        }
    }
    
    private void sendGroupMessage() {
        String content = inputField.getText();
        if (!content.isEmpty()) {
            messageClientService.sendMessageToAll(content, currentUserId);
            chatArea.append("你对大家说: " + content + "\n");
            inputField.setText("");
        }
    }
    
    private void sendFile() {
        String receiver = userList.getSelectedValue();
        if (receiver == null) {
            JOptionPane.showMessageDialog(this, "请选择接收者");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String destPath = "C:\\QQFiles\\" + file.getName(); // 接收方保存路径
            
            messageClientService.sendFileToOne(file.getAbsolutePath(), destPath, currentUserId, receiver);
            chatArea.append("发送文件给 " + receiver + ": " + file.getName() + "\n");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(QQView::new);
    }
}
