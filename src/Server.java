/**
 * Created by Abhijit on 6/3/17.
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
public class Server
{
    static ServerSocket server=null;
    static Socket socket=null;
    static ClientThread th[]=new ClientThread[10];
    static Client clientarr[] = new Client[10];
    static Encrypt encryptInstance = null;

    public static void main(String args[])
    {
        int port=1234;
        System.out.println("Server started...");
        System.out.println("  [Press Ctrl C to terminate ]");
        try
        {
            server=new ServerSocket (port);
        }
        catch(IOException e)
        {
            System.out.println("Exception for Input/Output");
        }
        while(true)
        {
            try
            {
                socket=server.accept();
                for(int i=0;i<=9;i++)
                {

                    if(th[i]==null)
                    {
                        clientarr[i] = new Client();
                        encryptInstance = new Encrypt();
                        (th[i]=new ClientThread(socket,th, clientarr, clientarr[i], encryptInstance)).start();
                        break;
                    }
                }
            }
            catch(IOException e)
            {
                System.out.println("Exception for Input/Output");
            }
        }
    }
}

class ClientThread extends Thread
{
    BufferedReader input=null;
    PrintStream output=null;
    Socket socket=null;
    Client client[] = null;
    ClientThread th[];
    Client clientInstance = null;
    Encrypt encryptInstance = null;
    static byte[] ebyte = null;
    public ClientThread(Socket socket,ClientThread[] th, Client[] client, Client clientInstance, Encrypt encryptInstance)
    {
        this.socket=socket;
        this.th=th;
        this.client = client;
        this.clientInstance = clientInstance;
        this.encryptInstance = encryptInstance;
    }
    public void run()
    {
        String msg;
        String username;
        ClientThread currentThread = this;
        try
        {
            clientInstance.setVisible(true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintStream(socket.getOutputStream());


            //output.println("What is your Name?Enter it-");
            username = JOptionPane.showInputDialog("Enter Your Name");//input.readLine();
            output.println(username + ":Welcome to chat room.");
            clientInstance.txtMessage.append("\nHi '"+ username + "' Welcome to chat room.");
            //output.println("To leave chat room type $$");
            for (int i = 0; i <= 9; i++)
                if (th[i] != null && th[i] != this) {
                    th[i].output.println("------------A new user arrived in chat Room:" + username);
                    client[i].txtMessage.append("\n------------A new user arrived in chat Room:" + username);
                }


            clientInstance.btnEncrypt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String inputMessage = clientInstance.txtMsgField.getText().toString();
                        String keynum = JOptionPane.showInputDialog("Enter your key");
                        byte[] raw = encryptInstance.retriveRawKey(keynum);
                        byte[] ibyte = inputMessage.getBytes();
                        ebyte = encryptInstance.encrypt(raw, ibyte);
                        String msg = new String(ebyte);
                        for (int i = 0; i <= 9; i++)
                            if (th[i] != null) {
                                th[i].output.println("<" + username + ">" + msg);
                                client[i].txtMessage.append("\n<" + username + ">" + msg);
                            }
                        clientInstance.txtMsgField.setText("");
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
            });

            clientInstance.btnSend.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String msg = clientInstance.txtMsgField.getText().toString();
                    for (int i = 0; i <= 9; i++)
                        if (th[i] != null) {
                            th[i].output.println("<" + username + ">" + msg);
                            client[i].txtMessage.append("\n<" + username + ">" + msg);
                        }
                    clientInstance.txtMsgField.setText("");
                }
            });

            clientInstance.btnDecrypt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String inputMessage = JOptionPane.showInputDialog("Enter Encrypted Message");
                        String keynum = JOptionPane.showInputDialog("Enter Key to Decrypt");
                        byte[] raw = encryptInstance.retriveRawKey(keynum);
                        String decryptedMessage = null;
                        //byte[] ibyte = inputMessage.getBytes();
                            byte[] dbyte = encryptInstance.decrypt(raw, ebyte);
                            decryptedMessage = new String(dbyte);
                        JOptionPane.showMessageDialog(clientInstance, decryptedMessage);
                    }catch (Exception e1){
                        //e1.printStackTrace();
                        JOptionPane.showMessageDialog(clientInstance, "Plz Enter a valid Key to decrypt msg \n OR \n unable to decrypt with this key");
                    }
                }
            });
            /*while (true)
            {
                msg = input.readLine();
                if (msg.startsWith("$$"))
                    break;
                for (int i = 0; i <= 9; i++)
                    if (th[i] != null) {
                        th[i].output.println("<" + username + ">" + msg);
                        client[i].txtMessage.append("\n<" + username + ">" + msg);
                    }
            }*/

            clientInstance.btnExit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try{
                        for (int k = 0; k <= 9; k++)
                            if (th[k] != null && th[k] != currentThread) {
                                th[k].output.println("------A user Leaving chat Room:" + username + "--------");
                                client[k].txtMessage.append("\n------A user Leaving chat Room:" + username + "--------");
                            }

                        for (int j = 0; j <= 9; j++)
                            if (th[j] == currentThread){
                                th[j] = null;
                                //System.out.println("Thread Closed");
                        }
                        input.close();
                        output.close();
                        socket.close();
                        clientInstance.dispose();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
            });
            /*for (int j = 0; j <= 9; j++)
                if (th[j] == this)
                    th[j] = null;
            input.close();
            output.close();
            socket.close();*/
        }
        catch (IOException e)
        {
            System.out.println("Exception for Input/Output");
        }
    }
}

