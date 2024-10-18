package network;

import exchange.Block;
import routing.DHT;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.nio.charset.StandardCharsets;

public class Peer {
    private String id;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, Socket> connectedPeers;
    private DHT dht;
    private ConcurrentHashMap<String, Block> blockStorage;

    public Peer(int port) throws IOException {
        this.id = generateId();
        this.serverSocket = new ServerSocket(port);
        this.connectedPeers = new ConcurrentHashMap<>();
        this.dht = new DHT();
        this.blockStorage = new ConcurrentHashMap<>();
        System.out.println("Peer started. ID: " + this.id);
    }

    private String generateId() {
        return "peer-" + System.currentTimeMillis();
    }

    public void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    String remoteAddress = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("Connected to peer: " + remoteAddress);
                    connectedPeers.put(remoteAddress, clientSocket);
                    // Exchange DHT with the newly connected peer
                    exchangeDHT(clientSocket);
                    new Thread(new PeerHandler(clientSocket, dht, blockStorage)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void connectToPeer(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            connectedPeers.put(host, socket);
            System.out.println("Connected to peer at " + host + ":" + port);
            // Exchange DHT with the peer we connected to
            exchangeDHT(socket);
            new Thread(new PeerHandler(socket, dht, blockStorage)).start();
        } catch (IOException e) {
            System.out.println("Unable to connect to peer at " + host + ":" + port);
            e.printStackTrace();
        }
    }

    // Method to exchange DHT information with a connected peer
    private void exchangeDHT(Socket socket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Send our DHT to the connected peer
            out.writeObject(dht.getAll());
            out.flush();
            System.out.println("Sent DHT to peer.");

            // Receive the peer's DHT and merge it with our own
            Map<String, String> receivedDHT = (Map<String, String>) in.readObject();
            dht.merge(receivedDHT);
            System.out.println("Merged DHT received from peer.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void storeBlock(byte[] data) {
        Block block = new Block(data);
        blockStorage.put(block.getHash(), block);
        System.out.println("Stored block with hash: " + block.getHash());
    }

    public void requestBlock(String host, String blockHash) {
        if (!connectedPeers.containsKey(host)) {
            System.out.println("No connection to peer: " + host);
            return;
        }

        try {
            Socket peerSocket = connectedPeers.get(host);
            PrintWriter out = new PrintWriter(peerSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));

            // Send a block request to the peer
            out.println("REQUEST_BLOCK " + blockHash);
            System.out.println("Requested block with hash: " + blockHash + " from peer: " + host);

            // Read the response from the peer with a timeout to prevent freezing
            peerSocket.setSoTimeout(5000);  // Set a 5-second timeout

            String response = in.readLine();
            if (response != null) {
                if (response.startsWith("BLOCK_DATA ")) {
                    String blockData = response.substring("BLOCK_DATA ".length());
                    byte[] data = blockData.getBytes(StandardCharsets.UTF_8);
                    Block receivedBlock = new Block(data);
                    blockStorage.put(receivedBlock.getHash(), receivedBlock);
                    System.out.println("Received block: " + receivedBlock);
                } else if (response.equals("BLOCK_NOT_FOUND")) {
                    System.out.println("Block not found at peer: " + host);
                } else {
                    System.out.println("Unexpected response from peer: " + response);
                }
            } else {
                System.out.println("No response received from peer.");
            }

        } catch (SocketTimeoutException e) {
            System.out.println("Request timed out. No response received from peer: " + host);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            Peer peer = new Peer(8081);
            peer.startListening();

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String command;
            while ((command = consoleReader.readLine()) != null) {
                String[] parts = command.split(" ", 3);
                if (parts[0].equalsIgnoreCase("connect") && parts.length == 3) {
                    String host = parts[1];
                    int port = Integer.parseInt(parts[2]);
                    peer.connectToPeer(host, port);
                } else if (parts[0].equalsIgnoreCase("store") && parts.length == 2) {
                    String data = parts[1];
                    peer.storeBlock(data.getBytes(StandardCharsets.UTF_8));
                } else if (parts[0].equalsIgnoreCase("request") && parts.length == 3) {
                    String host = parts[1];
                    String blockHash = parts[2];
                    peer.requestBlock(host, blockHash);
                } else {
                    System.out.println("Invalid command. Use: connect <host> <port>, store <data>, or request <host> <blockHash>");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
