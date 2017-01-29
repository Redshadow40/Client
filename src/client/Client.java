package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class Client {
    static FileHandler handler;
    static Logger logger;
    
    public static void main(String[] args) throws Exception{
        try {
            //setup logger
            handler = new FileHandler("client.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger = Logger.getLogger(Client.class.getName());
            logger.addHandler(handler);
            
            //check what ip and port are specified -- default is localhost:6175
            String ip = "localhost";
            String port = "6175";
            String input = null;
            
            if (args.length == 2 || args.length == 4){
                int i = 0;
                switch (args[i]){
                    case "-ip":
                        ip = args[1];
                        i += 2;
                        break;
                    case "-p":
                        port = args[1];
                        i += 2;
                        break;
                    default:
                        System.out.println("Usage:\nClient -ip <ipAddres|hostname> -p <port>");
                        logger.severe("incorrect arguments");
                        logger.exiting(Client.class.getName(), "main");
                        System.exit(1);
                }
            }
            else if(args.length > 0){
                System.out.println("Usage:\nClient -ip <ipAddres|hostname> -p <port>");
                logger.severe("incorrect arguments");
                logger.exiting(Client.class.getName(), "main");
                System.exit(1);
            }
            
            logger.info("address set to: http://" + ip + ":" + port + "/");
            System.out.println("\\q to exit");
            ArrayList<Thread> th = new ArrayList();
            Scanner in = new Scanner(System.in);
            while (true){
                input = in.nextLine();
                if (input.equalsIgnoreCase("\\q"))
                    break;
                else
                {
                    th.add((new Thread(new sendCommand(ip, port, input, logger)))); //seperate is command into a thread
                    th.get(th.size()-1).start();
                }
            }
            for (Thread t: th)
                t.join(); //don't exit until all threads are completed
        }catch (IOException | SecurityException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }
}
class sendCommand implements Runnable{
        String ip;
        String port;
        String input;
        Logger logger;
        
        public sendCommand(String ip, String port, String input, Logger logger){
            this.ip = ip;
            this.port = port;
            this.input = input;
            this.logger = logger;
        }
        
        @Override
        public void run(){
            String url = "http://" + ip + ":" + port + "/";
            int ticket = new Random().nextInt((10000-1000) + 1) + 1000;
            String xml = "<Request command=\"" + input + "\"><ticketid>" + ticket + "</ticketid></Request>";
            logger.info(Thread.currentThread().getId() + " -- Generated xml: " + xml);
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
 
            try {
                HttpEntity entity = new ByteArrayEntity(xml.getBytes("UTF-8"));
                post.setEntity(entity);
                logger.info(Thread.currentThread().getId() + " -- Sending: " + xml);
                HttpResponse response = client.execute(post);
                
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = in.readLine()) != null){
                    logger.info(Thread.currentThread().getId() + " -- Received: " + line);
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(sendCommand.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(sendCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

