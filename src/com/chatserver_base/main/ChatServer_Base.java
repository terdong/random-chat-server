package com.chatserver_base.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.chatserver_base.thread.ChatServerThread;


/**
 * @Class	 : ChatServerMain
 * @Date	 : 2010. 10. 11.
 * @Writer   : Gehem_um
 * @Edit     :
 * @Comment  : 소켓 생성 메인 서버
 */
public class ChatServer_Base {
    private static final int PORT = 10094;
    public void start(int port) {
        ServerSocket server = null;
        Socket socket = null;
        ChatServerThread thread;
        try {
            server=new ServerSocket(port);
            System.err.println("채팅 서버 실행 시작:"+port);
            while(true) {
                try {
                    socket=server.accept();
                    System.out.println(socket.getRemoteSocketAddress( ));
                    thread=new ChatServerThread(socket);
                    thread.start();
                } catch (Exception e) {
                    
                }
             }
        } catch (Exception e) {
        }finally{
            try
            {
                server.close( );
            }
            catch ( IOException e )
            {
            }
        }
    }
    public static void main(String[] args) {
        ChatServer_Base server=new ChatServer_Base();
        server.start(PORT);
    }
}


















