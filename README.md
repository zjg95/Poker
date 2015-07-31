# poker
A simple client/server application to simulate a poker game.
Clients can connect to the server and play with each other over the internet.

#### Running the program
There are two run harnesses for this application; the client side and the server side.
On the server's end, the file PokerServer.java should be run. On the client's end, PokerClient.java should be run.

#### PokerServer.java
This application will listen for clients on port 4444 (default). It will create a new thread to hold the poker lobby, and insert clients into the lobby as they connect.

#### PokerClient.java
This application will prompt the user for a server ip address, and then attempt to connect. Upon successful connection, the poker protocol will begin.
