package com.hpe.jms.client;

import java.io.BufferedReader;
import java.io.IOException;
import javax.jms.*;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.hpe.jms.util.JmsUtil;
public class JMSQueueMessageSender
{
	private QueueSender queueSender;
	private Queue queue;
	private TextMessage textMessage;
	
	private void init(Context ctx, String queueName)throws NamingException, JMSException, Exception
	{
		QueueSession queueSession = JmsUtil.getQueueSession(ctx);
		queue = (Queue) ctx.lookup(queueName);
		queueSender = queueSession.createSender(queue);
		textMessage = queueSession.createTextMessage();
	}
	
	private void send(String message) throws JMSException {
		textMessage.setText(message);
		queueSender.send(textMessage);
	}
	
	private void close() throws JMSException {
		if(queueSender !=null)
			queueSender.close();
		JmsUtil.cleanUp();
	}
	
	private static void readAndSend(JMSQueueMessageSender jMSQueueMessageSender) throws IOException, JMSException
	{
		BufferedReader br = null;
		StringBuffer buff = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader(JMSQueueMessageSender.class.getResourceAsStream("REQUEST.xml")));
			String line;
			while ((line = br.readLine()) != null) 
				{
					buff.append(line);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			
			jMSQueueMessageSender.send(buff.toString());
			System.out.println("Msg read...");
			}


	
	public static void main(String[] args) throws Exception {
		JMSQueueMessageSender jMSQueueMessageSender = null;
		try {
			System.out.println("Process started...");
			InitialContext ic = JmsUtil.getInitialContext();
			
			jMSQueueMessageSender = new JMSQueueMessageSender();
			
			jMSQueueMessageSender.init(ic, JmsUtil.QUEUE_JNDI_NAME);
			System.out.println("Jms Initialization done ...");
			readAndSend(jMSQueueMessageSender);
			System.out.println("Msg_sent_successfully....");
		
			
		} catch (Exception e) {
			System.out.println("Error...");
			e.printStackTrace();
		}finally {
			if(jMSQueueMessageSender != null)
			jMSQueueMessageSender.close();
			
		}
		
	}
}