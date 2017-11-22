package sample;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private CopyOnWriteArrayList<SocketThread> socketList;//набор сокетов, каждый для одтельно подсоединения клиента
    private String port;

    private DataOutputStream out;
    ExecutorService executor;
    Thread thread;



    public Server(String port1) {
        socketList=new CopyOnWriteArrayList<>();
        port = port1;
    }

    private void sendMessageIn(SocketThread st,String name){
        try {
            out = st.getOut();
            System.out.println("Вошел клиент "+name);
            try {
                if(st.getSocket().isConnected()) {
                    out.writeUTF("Вошел клиент " + name);
                }
            }catch (SocketException e1){
                e1.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //отпраить сообщению клиенту о том, что кто-то вышел
    private void sendMessageOut(SocketThread st, String name){
        try {
            out = st.getOut();
            System.out.print("Вышел клиент "+name);
            try {
                if(st.getSocket().isConnected()) {
                    out.writeUTF("Вышел клиент " + name);
                }
            }catch (SocketException e1){
                e1.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //отпарвить сообщения всем о том, что кто-то вошел
    public void sendMessageInAll(String name){
        for(SocketThread st:socketList){
            if(st!=null){
                sendMessageIn(st,name);
            }
        }
    }
    //отпарвить сообщения всем о том, что кто-то вышел
    public void sendMessageOutAll(String name){
        for(SocketThread st:socketList){
            if(st!=null){
                sendMessageOut(st,name);
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Запускаем сервер");
        int portNumber=Integer.parseInt(this.port);
        thread = Thread.currentThread();

        try {
            serverSocket=new ServerSocket(portNumber);

        } catch (IOException e) {
            e.printStackTrace();
        }

        int count =10;//максимальное количество подсоединений
        executor = Executors.newFixedThreadPool(count);//создаем Executor
        Socket socket;
        SocketThread st=null;
        for(int i=0;i<count;i++){
            try {
                socket= serverSocket.accept();//ожидаем подлючения
                st=new SocketThread(socket,this);
                socketList.add(st);//добавляем клиента в список
            } catch (IOException e) {
                e.printStackTrace();
            }
            executor.execute(st);//выполняем потоки
        }
        if (thread.isInterrupted()){
            executor.shutdownNow();
        }
        executor.shutdown();//закрываем потоки
    }
}
