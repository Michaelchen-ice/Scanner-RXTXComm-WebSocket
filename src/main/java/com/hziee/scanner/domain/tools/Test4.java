package com.hziee.scanner.domain.tools;

import gnu.io.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;


public class Test4 implements DataCallBack {

    public void runMethod(String text) {

        //获取WebSocketServer对象的映射。
        CopyOnWriteArraySet<WebSocket> webSocketSet = WebSocket.getWebSocketSet();
        if (webSocketSet.size() != 0){
            for (WebSocket item : webSocketSet) {
                try {
                    //向客户端推送消息
                    item.getSession().getBasicRemote().sendText(text);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }else {
            System.out.println("WebSocket未连接");
        }

    }



    public Test4() {
        super();
    }

    /**
     * 检测系统中可用的端口
     */
    private CommPortIdentifier portId;
    /**
     * 枚举类
     */
    private Enumeration portList;
    /**
     * 串口返回信息
     */
    private volatile String result = "";

    private static final String LOCK = "";

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String init(int baudRate) {
        //获取系统中可用的端口
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            System.out.println("发现端口：" + portId.getName());
            return portId.getName();
        }
        return null;
    }

    public void connect(String portName) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                InputStream in = serialPort.getInputStream();

                SerialReader serialReader = new SerialReader(in);
                serialReader.setCallBack(this);
                (new Thread(serialReader)).start();

            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    @Override
    public void onGetData(String data) {
        this.result = data;
        System.out.println("callback--" + data);
        runMethod(result);
//        try {
//            System.out.println(this.session.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    /**
     * 读串口数据
     */
    public static class SerialReader implements Runnable {
        InputStream in;
        DataCallBack callBack;

        public void setCallBack(DataCallBack callBack) {
            this.callBack = callBack;
        }

        public SerialReader(InputStream in) {
            this.in = in;
        }

        String getValue = "";

        public void run() {

            byte[] buffer = new byte[1024];
            int len = -1;
            try {

                do {
                    len = this.in.read(buffer);
                        getValue += new String(buffer,0,len);

                    if (getValue != null && getValue.length() > 2){
                        System.out.println(getValue);
                        System.out.println("#############################");

                        if (callBack != null) {
                            callBack.onGetData(getValue);
                            getValue = "";
                        }
                    }
                }while (len > -1);

//                while ((len = this.in.read(buffer)) > -1) {
//                    getValue = new String(buffer, 0, len);
//
//                    if (getValue.length() > 2) {
//                        System.out.println(getValue);
//                        System.out.println("#############################");
//
//                        if (callBack != null) {
//                            callBack.onGetData(getValue);
//                        }
//                    }
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public String getData() {
            return getValue;
        }
    }


//    public static void main(String[] args) {
//        try {
//            String port = (new Test4()).init(9600);
//            Test4 t = new Test4();
//            t.connect(port);
//            while (t.result == null || t.result == "" || t.result.equals("null")) {
//            }
//            System.out.println(t.result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}