package com.chatserver_base.thread;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.chatserver_base.message.Msg;

/**
 * @FileName : ChatServerUtill.java
 * @Project	 : MiniChatServer
 * @Package : 
 * @Date	 : 2010. 10. 11.
 * @Writer   : Gehem_um
 * @Version  : 
 * @Edit     :
 * @Comment  : 
 */

/**
 * @Class	 : ChatServerUtill
 * @Date	 : 2010. 10. 11.
 * @Writer   : Gehem_um
 * @Edit     :
 * @Comment  : ChatServerThread 에서 이용하는 Method Utility
 */
public class ChatServerUtill {
    private static volatile ChatServerUtill singleton;
    private StringBuffer sb;
    /**
     * ChatServerUtill's Constructor
     * @Comment  :
     */
    private ChatServerUtill() {sb=new StringBuffer();}
    
    /**
     * @return ChatServerUtill
     * @Comment : Singleton pattern 의 가장 기본적인 구조의 자신 객체 반환 method.
     *             덧붙이자면, 내부적으로 선언한 자신의 객체의 값이 null 이면, 새로 생성하되, 직렬화 모듈을 이용함
     *             그리하여, static memory에 상주되어 종료시까지 생존하고 있는 자신 객체를 반환
     *             Utill Class가 가변화될 일은 없기 때문에(Method 내부적으로는 가변되나 다시 초기화됨)
     *             Singleton pattern이 가장 효율적이다!!(객체 자체를 static으로 선언하는 것은 비추)
     *             StringBuffer를 굳이 생성한 이유도, 자원낭비를 줄이고자 썼지만, 효율적인가는 테스트 해봐야함.
     *             대부분의 method 들이 다 문자열 조합 관련 계통이나 그 밖의 중요 method 는 개별적으로 comment 처리함.
     */
    public static ChatServerUtill getInstance() {
        if(singleton==null) {
            synchronized (ChatServerUtill.class) {
                if(singleton==null) {
                    singleton = new ChatServerUtill();
                }
            }
        }
        return singleton;
    }
    private String setChatName(String nick) {
        return "["+nick+"]";
    }
    private String msgChangeNick(String preNick, String uNick) {
        return setChatName(preNick)+Msg.getStr("ChatServ.1")+setChatName(uNick)+Msg.getStr("ChatServ.7","ChatServ.19");
    }
    public String notChangNick(String nick) {
        return setChatName(nick)+Msg.getStr("ChatServ.7","ChatServ.20","ChatServ.21","ChatServ.22");
    }
    public String msgChatName(String nick) {
        return setChatName(nick)+Msg.getStr("ChatServ.11");
    }
    public String msgPreWelCome() {
        return Msg.getStr("ChatServ.23");
    }
    public String msgWelCome(String nick) {
        return setChatName(nick)+Msg.getStr("ChatServ.1","ChatServ.2","ChatServ.5");
    }
    public String msgHelp() {
//        return Msg.getStr("ChatServ.30")+Msg.getStr("ChatServ.31");
        return Msg.getStr("ChatServ.30");
    }
    public String msgClose(String nick) {
        return setChatName(nick)+Msg.getStr("ChatServ.1","ChatServ.3","ChatServ.5");
    }
    public String msgUsers(int size) {
        return Msg.getStr("ChatServ.17")+String.valueOf(size)+Msg.getStr("ChatServ.18","ChatServ.9");
    }
    public String msgAliveUsers(String nick, boolean alive) {
        sb.setLength(0);
        sb.append(setChatName(nick)+Msg.getStr("ChatServ.1","ChatServ.8"));
        sb.append(alive?Msg.getStr("ChatServ.9"):Msg.getStr("ChatServ.10"));
        return sb.toString(); 
    }
    public String msgKickUser(String nick) {
        return setChatName(nick)+Msg.getStr("ChatServ.1","ChatServ.4","ChatServ.3","ChatServ.6");
    }
    
    public String setDefaultNick(int count) {
        return Msg.getStr("ChatServ.0")+count;
    }
    /**
     * @param us
     * @param nick
     * @return String
     * @Comment : UserInfo 객체 참조변수를 받아서(call by reference) 그 객체 내의 nick Name 을 변경시켜주는 작업
     */
    public String changNick(UserInfo us,String nick) {
        us.setPreNick(us.getuNick());
        us.setuNick(nick);
        return msgChangeNick(us.getPreNick(),us.getuNick());
    }
    /**
     * @param socket
     * @return String
     * @Comment : Socket 을 받아서 해당 Socket의 IP를 친절하게 문자열로 반환 
     */
    public String getIPAddress(Socket socket) {
        String str = socket.getInetAddress().toString();
        str=str.replace("/", "");
        return Msg.getStr("ChatServ.15","ChatServ.16")+str+Msg.getStr("ChatServ.9");
    }
    /**
     * @param socket
     * @return InputStream
     * @Comment : 매개변수로 받은 socket의 InputStream 반환. 별거없음. 단지, 예외처리 구문 처리 이유가 큼
     */
    public InputStream getSocketInputStream(Socket socket) {
        try {return socket.getInputStream();}catch (IOException e) {e.printStackTrace();}
        return null;
    }
    /**
     * @param socket
     * @return OutputStream
     * @Comment : 매개변수로 받은 socket의 OutputStream 반환. 별거없음
     */
    public OutputStream getOutputStream(Socket socket) {
        try {return socket.getOutputStream();} catch (IOException e) {e.printStackTrace();}
        return null;
    }
    
    /**
     * @param obj void
     * @Comment : 매개변수를 역 분석하여 객체를 찾아서 해당 객체에 대한 close 처리를 해줌
     *             아주아주 편리하면서 개념찬 module
     */
    public void closeObject(Object obj) {
        try {
            if(obj instanceof Socket)
                ((Socket)obj).close();
            else if(obj instanceof InputStream)
                ((InputStream)obj).close();
            else if(obj instanceof OutputStream)
                ((OutputStream)obj).close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
    }

}

















