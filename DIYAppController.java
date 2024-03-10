/** 
 * This is the main server file for distributing the data slice to the workers
 * 
 * Used to start controller and listen for incoming workers via it's 1 argument: Port #
 * Use in terminal: java DIYAppController port -- to start server
 * 
 * The worker class is DIYAppWorker.java
 * 
 * @Author Brady Blackstone
*/

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class DIYAppController 
{
    private static LinkedBlockingQueue<String> boundBuffQueue; // shared queue between all workers
    private static DoubleAdder totalSum = new DoubleAdder(); // sum for all workers
    private static final ReentrantLock rl = new ReentrantLock(); // lock used for synchronization between all workers
    private static final Condition cond = rl.newCondition(); // used to suspend execution of a worker thread
    
    // main thread
    public static void main(String...args) 
    {
        DIYAppController dac = new DIYAppController();
        ServerHandler ctrl; // handles the creation of the server and every new worker thread
        DoubleAdder mainSum = new DoubleAdder(); // sum for controller only
        final int sliceTotal = 100, dataSlice = 50; // queue can hold 5,000 instances at a given time
        boundBuffQueue = new LinkedBlockingQueue<String>(sliceTotal); // queue is the size of the current sliceTotal
        Scanner sc;

        Scanner fileName = new Scanner(System.in);
        System.out.println("\n\nEnter in a .dat / .txt file containg floating-point values (>= 5,000 instances)");

        try 
        {
            if (args.length == 0)
            {
                System.err.println("Error, no port provided\n");
                System.exit(0);
            }
            else
            {
                File file = new File(fileName.nextLine());
                fileName.close();
                sc = new Scanner(file);
            
                // becomes true once the slice has filled up
                //      to the predefined limit of 50
                Boolean startServer = false;
                // int count = 0; -> used for debugging purposes to count the initial number
                //                   of slices added to the boundBuffQueue
                int port = Integer.parseInt(args[0]);

                if (port <= 1023)
                {
                    System.out.println("Not a usable port number. Must be > 1023.");
                    System.exit(0);
                }

                ctrl = dac.new ServerHandler(port);
                while (sc.hasNextDouble())
                {
                    // fill up queue before starting server
                    // if queue is low, keep refilling
                    //      until there is no more data from the file
                    // maintains the queue throughout the addition
                    if (boundBuffQueue.size() < sliceTotal)
                    {
                        String slice = "";
                        double data;
                        for (int i = 0; i < dataSlice; ++i)
                        {
                            try 
                            {
                                data = sc.nextDouble();
                                slice += data + "~";
                                mainSum.add(data);
                            }
                            catch (Exception e) 
                            {
                                System.out.println("Final float value has been added to the que");
                                break;
                            }
                        }
                        
                        try 
                        {
                            // count++;
                            // System.out.println(count);
                            boundBuffQueue.put(slice);
                           
                        } catch (InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                        // once queue has hit the predefined limit of 100,
                        // start the controller
                        if (boundBuffQueue.size() == sliceTotal)
                        {
                            if(startServer == false)
                            {
                                System.out.println("\nController has started\n");
                                ctrl.open();
                                ctrl.start();
                                System.out.println("DIY Controller is listening on port " + port + "\n");
                                startServer = true;
                            }
                        }
                    }
                }
                
                while (!boundBuffQueue.isEmpty()) 
                {
                    continue;
                }
                
                try
                {
                    Thread.sleep(5000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                // print out the total sum without using worker clients
                System.out.println("\nThe controller total is " + mainSum.doubleValue());
                // print out the total sum between all workers
                System.out.println("\nThe total is " + totalSum.doubleValue());  

                ctrl.interrupt();
                System.out.println("\nController has terminated\n");
                sc.close();
                System.exit(0);
            }
        } catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
    }

    // server thread
    class ServerHandler extends Thread
    {
        private ServerSocket ctrlSock;
        private int port;

        public ServerHandler(int  port)
        {
            this.port = port;
        }

        // open server controller port given by parameter
        protected void open()
        {
            try
            {
                ctrlSock = new ServerSocket(port);
            } catch (Exception e) 
            {
                e.printStackTrace();
            }
        }

        // listens for new worker clients on server port and creates a new thread for each
        public void run()
        {
            try
            {
                while (true)
                {
                    Socket workSock = ctrlSock.accept();
                    System.out.println("\nWorker has connected\n");
                    new WorkerHandler(workSock).start();
                }
            } catch (Exception e)
            {
                // shut down server and stop program if there's an exception
                try
                {
                    ctrlSock.close();
                    System.exit(0);
                } catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }

    // worker agent thread
    class WorkerHandler extends Thread
    {
        private Socket workSock;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private String buffer; 

        public WorkerHandler(Socket workSock)
        {
            this.workSock = workSock;
        }

        // reads (grabs) slice from queue and writes (sends) to client
        // adds the new partial sum to the total until queue is empty
        // slows down thread so that multiple threads can have
        //      an equal chance to grab data slice and sum it
        public void run()
        {
            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            buffer = "";
            write("Connected to controller. Ready for processing.");
            do
            {
                try 
                {
                    buffer = read();

                    // send null to worker after the queue is empty then terminate worker thread
                    if (buffer == null)
                    {
                        write(buffer);
                        break;
                    }

                    // send 0.0 if the buffer is empty to start the worker thread
                    if (buffer.equals("0.0"))
                    {
                        totalSum.add(Double.parseDouble(buffer));
                        buffer = boundBuffQueue.poll();
                        write(buffer);
                    }
                    // critical area where only one worker thread has access 
                    // to the totalSum variable
                    else
                    {
                        rl.lock();
                        cond.awaitNanos(500000000);
                        totalSum.add(Double.parseDouble(buffer));
                        buffer = boundBuffQueue.poll();
                        rl.unlock();
                        write(buffer);
                    }
                } 
                catch (Exception e) 
                { 
                    continue;
                }
           
            } while (true);
            System.out.println("Done!\n");
        }
    
        // used to read (grab) partial sum from worker
        // if data slice from worker is 0, then it closes this thread
        private String read()
        {
            String data = "";
            try 
            {
                ois = new ObjectInputStream(workSock.getInputStream());
                data = (String)ois.readObject();
                if (data == null)
                {
                    workSock.close();
                }
            } catch (Exception e) {}

            return data;
        }

        // used to send (write) data slice to the client
        private void write(String data) 
        {
            try 
            {
                oos = new ObjectOutputStream(workSock.getOutputStream());
                oos.writeObject(data);
            } catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
}
