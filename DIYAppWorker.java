/** 
 * This is the main client file for calculating the partial sum 
 *      of the data slice sent to it by the controller
 * 
 * Used to connect the worker to the controller via it's 2 arguments:
 *      IP (or localhost), Port #
 * Use in terminal: java DIYAppWorker IP port to connect the client to the server
 * 
 * The controller class is DIYAppController.java
 * 
 * @Author Brady Blackstone
*/

import java.io.*;
import java.net.Socket;

public class DIYAppWorker 
{
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket sock;
    
    public static void main(String[] args)
    {
        String buffer = "";
        if (args.length != 2)
        {
            System.err.println("Usage: java DIYAppWorker server port");
            System.exit(0);
        }
        DIYAppWorker daw = new DIYAppWorker();
        daw.connect(args);
        System.out.println(daw.read()); //clear buffer

        // request (send / write) first partial sum of 0 to controller for more work
        daw.write("0.0");
        
        // read slice sent by controller and calculate the partial sum
        // if slice reads null, worker terminates
        System.out.println("\nProcessing ...");
        while (true)
        {
            try
            {
                buffer = daw.read();
                if (buffer == null)
                {
                    break;
                }
                buffer = daw.addSlice(buffer);
                daw.write(buffer);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("\nWorker has terminated\n");
    }

    // returns total partial sum of the data slice
    private String addSlice(String str)
    {
        // tokenizer that deliniates each float instance with a "~"
        String[] tokens = str.split("~");
        Double pSum = 0.0;

        // converts each string value into a double
        // and adds it to the partial sum
        for (String s : tokens)
        {
            // if the token is in scientific notation (i.e. with an 'e' or 'E'),
            //  convert it to standard notation
            if (s.toLowerCase().contains("e"))
            {
                // split the token by 'e' (including empty strings as well (-1))
                // into the mantissa and exponent
                String[] e = s.split("e", -1);

                // if it is not an empty string, split it,
                // else add the empty string to the partial sum
                if (e.length > 1)
                {
                    double mantissa = Double.parseDouble(e[0]);
                    int exp = Integer.parseInt(e[1]);
                    pSum += mantissa * Math.pow(10, exp);
                }
                else
                {
                    pSum += Double.parseDouble(s);
                }
            }
            else
            {
                pSum += Double.parseDouble(s);
            }
        }

        return String.valueOf(pSum);
    }
    
    // connects worker to controller
    private Boolean connect(String...args)
    {
        String ip = "localhost"; //127.0.0.1
        int port = Integer.parseInt(args[1]);
        boolean connected = false;
        try
        {
            sock = new Socket(ip, port);
            System.out.println("\nConnecting to controller on port " + port + "\n");
            connected = true;
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return connected;
    }

    // used to read data slices from the controller
    // if slice reads 0, close worker socket
    private String read()
    {
        String data = "";
        
        try 
        {
            ois = new ObjectInputStream(sock.getInputStream());
            data = (String)ois.readObject();
        } catch (Exception e) 
        {
            e.printStackTrace();
        }

        return data;
    }

    // used to write (send) partial sums back to controller
    private void write(String data) 
    {
        try 
        {
            oos = new ObjectOutputStream(sock.getOutputStream());
            oos.writeObject(data);
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
