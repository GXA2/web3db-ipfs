package network;

import exchange.Block;
import routing.DHT;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class PeerHandler implements Runnable {
    private Socket socket;
    private DHT dht;
    private ConcurrentHashMap<String, Block> blockStorage;

    public PeerHandler(Socket socket, DHT dht, ConcurrentHashMap<String, Block> blockStorage) {
        this.socket = socket;
        this.dht = dht;
        this.blockStorage = blockStorage;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("REQUEST_BLOCK ")) {
                    // Handle block request
                    String[] parts = inputLine.split(" ");
                    String blockHash = parts[1];
                    Block block = blockStorage.get(blockHash);
                    if (block != null) {
                        out.println("BLOCK_DATA " + new String(block.getData(), StandardCharsets.UTF_8));
                        System.out.println("Sent block with hash: " + blockHash);
                    } else {
                        out.println("BLOCK_NOT_FOUND");
                        System.out.println("Block not found: " + blockHash);
                    }
                } else if (inputLine.equals("REQUEST_DHT")) {
                    // Handle DHT request if peers explicitly request DHT data exchange
                    ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                    objectOut.writeObject(dht.getAll());
                    objectOut.flush();
                    System.out.println("Sent DHT to peer on request.");
                } else {
                    System.out.println("Message from peer: " + inputLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

