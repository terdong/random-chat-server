package com.chatserver_base.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.chatserver_base.message.Msg;


/**
 * @Class        : ChatServerThread
 * @Date         : 2010. 10. 11.
 * @Writer   : Gehem_um
 * @Edit     :
 * @Comment  : User Management Thread
 *              상당히 C-Lang스러운 code 들이 많이 보임
 *              refactoring 하는데 지침...
 */
public class ChatServerThread extends Thread{
    private static List<ChatServerThread> threads = Collections.synchronizedList(new ArrayList<ChatServerThread>());
    private static final String UTF8 = Msg.getStr("ChatServ.100");
    private static final int QUIT = -1;
    private static final int NOTICE = 0;
    private static final int MSGALL = 1;
    private static final int SELF = 2;
    private static final int KICK = 3;
    private static int count=0;
    
    private ChatServerUtill csu;
    private UserInfo us;
    private SendMsg sm;
    private Socket sock;
    private InputStream in;
    private OutputStream out;
    
    public ChatServerThread(Socket sock) {
        init(sock);
    }
    
    public void run() {
        MainProcess();
    }
    
    /**
     * @param sock void
     * @Comment : 각종 객체들 초기화 및 Thread List 에 해당 thread 추가
     */
    private void init(Socket sock) {
        csu=ChatServerUtill.getInstance();
        this.sock=sock;
        this.in = csu.getSocketInputStream(sock);
        this.out = csu.getOutputStream(sock);
        us = new UserInfo(csu.setDefaultNick(count++)); 
        sm = new SendMsg(NOTICE,csu.msgWelCome(us.getuNick()));
        threads.add(this);
    }
    
    /**
     *  void
     * @Comment : Chat Server의 Main Loop Process
     */
    private void MainProcess() {
        // Message Size - Receive
        int size;
        // Message Quota(Init) - Receive
        byte[]w=new byte[1024];
        // Send - Welcome Message 
        sendMessageAll(sm);
        while(sock!=null && sock.isConnected()) {
            try {
                size=in.read(w);
                if(size<=0) throw new IOException();
                // Encoding >> UTF8
                sm=cmdCheck(new String(w,0,size,UTF8));
                msgProcess(sm);
                sm.ClearMsg();
            } catch(IOException e) {
                closeChat(this);
            }
        }
    }

    /**
     * @param sm void
     * @Comment : message의 flag 에 따라 적절한 처리를 함.
     */
    private void msgProcess(SendMsg sm) {
        if(sm.msg!=null)
            switch (sm.flag) {
                case SELF:
                    sendMessage(this, sm.msg);
                    break;
                case KICK:
                    sendMessage(sm.thread, Msg.getStr("ChatServ.52"));
                case QUIT:
                    closeChat(sm.thread);
                    sendMessageAll(sm);
                    break;
                default:
                    sendMessageAll(sm);
                    break;
            }
    }
    
    /**
     * @param msg
     * @return SendMsg
     * @Comment : user가 입력한 msg 를 해독하여 명령어면 해당 명령어 수행
     */
    private SendMsg cmdCheck(String msg) {
        if(msg.charAt(0)=='/') {
            String [] result = msg.split(Msg.getStr("ChatServ.101")); //공백으로 문장 절단
            int flag=NOTICE;
            msg=null;
            switch (result.length) {
                case 1:
                    if(result[0].equals(Msg.getStr("ChatServ.50"))) { //help
                        msg=csu.msgHelp();
                        flag=SELF;
                    }else if(result[0].equals(Msg.getStr("ChatServ.51"))) { //myip
                        msg=csu.getIPAddress(sock);
                        flag=SELF;
                    }else if(result[0].equals(Msg.getStr("ChatServ.52"))) {//quit
                        msg =csu.msgClose(us.getuNick());
                        sm.thread=this;
                        flag=QUIT;
                    }else if(result[0].equals(Msg.getStr("ChatServ.56"))) {// users
                        msg = csu.msgUsers(threads.size());
                        flag=SELF;
                    }
                    break;
                case 2:
                    if(result[0].equals(Msg.getStr("ChatServ.53"))) {//nick
                        if(!searchUserAlived(result[1]))
                            msg = csu.changNick(us,result[1]);
                        else {
                            msg = csu.notChangNick(result[1]);
                            flag=SELF;
                        }
                        
                    }else if(result[0].equals(Msg.getStr("ChatServ.54"))) {//search
                        msg = csu.msgAliveUsers(result[1], searchUserAlived(result[1]));
                        flag=SELF;
                    }else if(result[0].equals(Msg.getStr("ChatServ.55"))) {//kick
                       sm.thread=kickUser(result[1]);
                       if(sm.thread!=null) {
                           msg = csu.msgKickUser(result[1]);
                       }else
                           msg = csu.msgAliveUsers(result[1], false);
                       flag=KICK;
                    }
                    
                    break;
            }
            sm.flag=flag;sm.msg=msg;
            return sm;
        }
        if(sm.flag!=MSGALL) sm.flag=MSGALL;
        sm.msg=msg;
        return sm;
    }
    
    /**
     * @param thread void
     * @Comment : 해당 thread를 Thread list에서 제거하고, socket 과 i/o stream 닫기
     */
    private void closeChat(ChatServerThread thread) {
        if(thread!=null) {
            csu.closeObject(thread.in);
            csu.closeObject(thread.out);
            csu.closeObject(thread.sock);
            threads.remove(thread);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * @param nick
     * @return boolean
     * @Comment : 유저 찾기, nick 으로 검색하여 있으면 true, 없으면 false
     */
    private boolean searchUserAlived(String nick) {
        for(ChatServerThread thread:threads)
            if(thread.isAlive()&&thread.us.getuNick().equals(nick))
                return true;
        return false;
    }
    
    /**
     * @param nick
     * @return ChatServerThread
     * @Comment : 강제 퇴장 시킬 유저 검색 후 있으면 thread 반환, 없으면 null 반환
     */
    private ChatServerThread kickUser(String nick) {
        for(ChatServerThread thread:threads)
            if(thread.isAlive()&&thread.us.getuNick().equals(nick))
                return thread;
        return null;
    }
    

    /**
     * @param sm void
     * @Comment : Thread List 에 속한 모든 user 들에게 Message Send
     */
    private void sendMessageAll(SendMsg sm) {
        if(sm.flag==NOTICE || sm.flag==QUIT || sm.flag==KICK)
            sm.addString(csu.msgChatName(Msg.getStr("ChatServ.14")));
        else if(sm.flag==MSGALL)
            sm.addString(csu.msgChatName(us.getuNick()));
        sm.addMsg();
        for(ChatServerThread thread:threads)
            if(thread.isAlive()) {
                sendMessage(thread,sm.getSb());
            }
        System.out.println(sm.getSb());
        sm.setInitSb();
    }

    /**
     * @param thread
     * @param msg void
     * @Comment : 해당 thread 해당 message 전송
     */
    private void sendMessage(ChatServerThread thread, String msg) {
        try {
            byte[] w=msg.getBytes(UTF8);
            thread.out.write(w);
            thread.out.flush();
        } catch (Exception e) {}
    }

    /**
     * @Class    : SendMsg
     * @Date     : 2010. 10. 15.
     * @Writer   : Gehem_um
     * @Edit     :
     * @Comment  : thread 별로 flag 및 msg를 저장 하는 Inner Bean Class
     *              추가로 Utill 성격의 Method 포함
     */
    public class SendMsg{
        private int flag;
        private String msg;
        private StringBuffer sb;
        private ChatServerThread thread;
        public SendMsg() {}
        public SendMsg(int flag, String msg) {
            this.flag=flag;
            this.msg=msg;
            this.sb = new StringBuffer();
            this.thread=null;
            
        }
        public void ClearMsg() {
            flag=MSGALL;
            msg=null;
            thread=null;
        }
        public void addString(String str) {
            sb.append(str);
        }
        public void addMsg() {
            sb.append(msg);
        }
        public String getSb() {
            return sb.toString();
        }
        public void setInitSb() {
            sb.setLength(0);
        }
    }
}//class
















