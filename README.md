# DIYDistributedApp 

This program will simulate a distribution application that calculates the sum of a large data set and prints it out to the terminal using a client-server model.  
<br>

This package contains 4 files:  
&ensp; &ensp; &ensp; &ensp; - DIYAppController.java    
&ensp; &ensp; &ensp; &ensp; - DIYAppWorker.java    
&ensp; &ensp; &ensp; &ensp; - genNum.py    
&ensp; &ensp; &ensp; &ensp; - README.md   
<br><br>

genNum.py is a Python script that generates 5000 random float numbers between the values of 0 and 1 with a precision of 10 digits  
&ensp; &ensp; &ensp; &ensp; - this script can be modified to the user's liking if they would like to have more/less numbers and/or more/less precision  
&ensp; &ensp; &ensp; &ensp; - to do this, modify this line:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - rand = [round(random.random(), 10) for _ in range(5000)]  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - 10 is the precision and 5000 is the total number of random float numbers  
&ensp; &ensp; &ensp; &ensp; - A user can also name the file that is generated to whatever name they want, as long as it's a .dat or .txt file (default is rand.dat)  
<br>

DIYAppController (Server) Description:  
&ensp; &ensp; &ensp; &ensp; - This class calculates the total sum between all workers and prints it out to the console  
<br>
&ensp; &ensp; &ensp; &ensp; - The first thing this class does is ask the user to enter in a .dat / .txt file containing floating-point values (>= 5,000 instances)  
&ensp; &ensp; &ensp; &ensp; - after the user enters in a valid file, the controller will continue by reading that file with Java's Scanner  
<br>
&ensp; &ensp; &ensp; &ensp; - Inside this class are 2 handler classes, one for the server and the other is for the clients:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; 1. ServerHandler - starts the server and creates new worker threads  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; 2. WorkerHandler - (1): handles the communication between the controller and the workers  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &nbsp; &nbsp; (2): reads data slices from the queue and sends them to the workers  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &nbsp; &nbsp; (3): adds the partial sums sent by the workers to the running total in the main thread  
<br>

DIYAppWorker (Client) Description:  
&ensp; &ensp; &ensp; &ensp; - This class accepts data slices from the controller, sums it up, and sends back the partial sum of the data slice  
<br>

Compile:  
&ensp; &ensp; &ensp; &ensp; - To compile the source code files use:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - javac DIYAppController.java  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - javac DIYAppWorker.java  
&ensp; &ensp; &ensp; &ensp; - These will create the following executable files:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - DIYAppController.class    
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - DIYAppController$ServerHandler.class    
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - DIYAppController$WorkerHandler.class    
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - DIYAppWorker.class
<br>

Run:  
&ensp; &ensp; &ensp; &ensp; - To run the program first start the controller, then start the workers:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - java DIYAppController (port #)  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - java DIYAppWorker localhost (or 127.0.0.1) (port #)  

&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - A user can either use 2 or more separate windows of an IDE to run the program or  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - A user can open multiple terminals and start the server on one then run the client command multiple times  

&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - Since I am using the localhost address, this code will only work on the same machine  

&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - The controller can only work with large data files with at least 5,000 instances or more  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - it can be modified to run with a smaller number of instances if the sliceTotal and dataSlice values are changed  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - Ex: sliceTotal = 20, dataSlice = 10 -> queue can hold 200 instances at a given time  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - Note:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - sliceTotal is the minimum number of dataSlices needed to start the controller and 
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; is the maximum number of dataSlices allowed at any given time  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - dataSlice is the maximum number of float values that are allowed to be  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; concatenated together in a string that is to be sent to the workers
<br>

&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; If a user is using VSCode, then they can create a launch.json file using the following link as a guide:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; https://go.microsoft.com/fwlink/?linkid=830387  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - After clicking create, select Java as your debug environment and use the following 
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; configuration settings:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - Note:  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - port # can be any number of a user's choosing as long as it's greater than 1023  
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; - These configurations should allow a user to easily debug and run multiple 
&ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; &ensp; terminals more conveniently than the above method

                {
                    "version": "0.2.0",
                    "configurations": [
                        {
                            "type": "java",
                            "name": "General Run",
                            "request": "launch",
                            "mainClass": "${file}",
                            "args": 
                            [
                                "port #"
                            ],
                            "cwd": "${workspaceFolder}"
                        },
                        {
                            "type": "java",
                            "name": "Controller",
                            "request": "launch",
                            "mainClass": "DIYAppController",
                            "args": 
                            [
                                "port #"
                            ],
                            "cwd": "${workspaceFolder}"
                        },
                        {
                            "type": "java",
                            "name": "worker",
                            "request": "launch",
                            "mainClass": "DIYAppWorker",
                            "args": 
                            [
                                "127.0.0.1","port #"
                            ],
                            "cwd": "${workspaceFolder}"
                        }
                    ]
                }