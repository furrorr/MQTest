import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.*;
import org.w3c.dom.Text;

public class MQTest {
    public static void main(String[] args) {
        try {
            MQQueueConnection mqConn;
            MQQueueConnectionFactory mqCF;
            final MQQueueSession mqQSession;
            MQQueue mqIn;
            MQQueue mqOut;
            MQQueueReceiver mqReceiver;
            MQQueueSender mqSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");
            mqCF.setPort(1414);
            mqCF.setQueueManager("ADMIN");
            mqCF.setChannel("SYSTEM.DEF.SVRCONN");

            mqConn = (MQQueueConnection) mqCF.createConnection();
            mqQSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (MQQueue) mqQSession.createQueue("MQ.IN");
            mqOut = (MQQueue) mqQSession.createQueue("MQ.OUT");

            mqReceiver = (MQQueueReceiver) mqQSession.createReceiver(mqIn);
            mqSender = (MQQueueSender) mqQSession.createSender(mqOut);

            javax.jms.MessageListener Listener = new javax.jms.MessageListener() {
                @Override

                //метод, который срабатывает всегда, когда в очереди появляется какой нибудь месседж
                public void onMessage(Message msg) {
                    System.out.println("Got message!");
                    if (msg instanceof TextMessage) {
                        try {
                            TextMessage tMsg = (TextMessage) msg;
                            String msgText = tMsg.getText();
                            System.out.println(msgText);
                            TextMessage message1 = (TextMessage) mqQSession.createTextMessage("Message 1");
                            mqSender.send(message1);
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            TextMessage message2 = (TextMessage) mqQSession.createTextMessage("message 2");
            message2.setJMSReplyTo(mqOut);
            mqReceiver.setMessageListener(Listener);
            mqSender.send(message2);
            mqConn.start();
            System.out.println("Stub Started!");

        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}