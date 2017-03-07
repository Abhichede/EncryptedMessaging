/**
 * Created by abmiro on 6/3/17.
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;


public class Client extends JFrame implements Runnable
{
    static Socket socket=null;
    static PrintStream output;
    static BufferedReader input=null;
    static BufferedReader userip=null;
    static boolean flag=false;
    JTextArea txtMessage;
    JTextField txtMsgField;
    JLabel lblMsg;
    JButton btnSend, btnExit, btnEncrypt, btnDecrypt;

    public Client(){
        super("Client");
        this.setLayout(new FlowLayout());
        this.setSize(500, 500);
        this.setLocation(400, 100);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //this.setVisible(true);

        txtMessage = new JTextArea("", 20, 40);
        JScrollPane scrollPane = new JScrollPane(txtMessage);
        txtMessage.setEditable(false);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel jPanel = new JPanel();
        lblMsg = new JLabel("Message : ");
        txtMsgField = new JTextField(10);
        btnSend = new JButton("Send");
        btnExit = new JButton("Exit(Close Chat)");
        JPanel jPanel1 = new JPanel();
        btnEncrypt = new JButton("Encrypt & send");
        btnDecrypt = new JButton("Decrypt");

        jPanel.add(lblMsg);
        jPanel.add(txtMsgField);
        jPanel.add(btnSend);
        jPanel.add(btnExit);
        jPanel1.add(btnEncrypt);
        jPanel1.add(btnDecrypt);

        this.add(scrollPane);
        this.add(jPanel);
        this.add(jPanel1);

    }


    public static void main(String[] args)
    {
        int port=1234;
        String host="127.0.0.1";
        JTextArea txtMessage = null;
        JTextField txtMsgField = null;
        try
        {
            socket=new Socket(host,port);
            userip=new BufferedReader(new InputStreamReader(System.in));
            output=new PrintStream(socket.getOutputStream());
            input=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(Exception e)
        {
            System.err.println("Unknown host"+host);
        }
        if(socket!=null)
        {
            try
            {
                new Thread(new Client()).start();
                while(!flag)
                {
                    output.println(userip.readLine());
                    //txtMessage.append("\n jjj   "+ userip.readLine());
                }
                output.close();
                input.close();
                socket.close();
            }
            catch(Exception e1)
            {
                System.err.println("IOException"+e1);
            }
        }

    }
    public void run()
    {
        String msg;
        //new Client().setVisible(true);
        try
        {
            while((msg=input.readLine())!=null)
                System.out.println(msg);
            txtMessage.append("\n"+ msg);

            flag=true;
        }
        catch(IOException e)
        {
            System.err.println("IOException" + e);
        }
    }
}