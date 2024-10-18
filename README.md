
# Minimalist Java IPFS Implementation

## Introduction

This project aims to implement a minimalist version of the InterPlanetary File System (IPFS) using Java. We will follow a layered architecture approach, starting from the network layer and working our way up to the naming layer. The project will focus on the core concepts of IPFS while maintaining simplicity and extensibility for future enhancements like erasure encoding.

## Roadmap

The implementation follows the IPFS architecture stack with a focus on achieving a working prototype in phases:

### Phase 1: Network Layer

**Goal**: Establish basic peer-to-peer communication using TCP/UDP sockets.

- **Tasks**:
  - Implement a `Peer` class to open server sockets and listen for connections.
  - Set up a simple protocol for peer-to-peer messaging.
  - Generate unique peer IDs using hash functions (e.g., public key hashes).
  
- **Deliverable**: Basic peer discovery and communication.

### Phase 2: Routing Layer (DHT)

**Goal**: Implement a Distributed Hash Table (DHT) for peer and data routing.

- **Tasks**:
  - Develop a `DHT` class for key-value storage of peer and data information.
  - Implement DHT propagation and lookups between peers.

- **Deliverable**: A functional DHT for storing and retrieving data and peer addresses.

### Phase 3: Exchange Layer (Data Transfer)

**Goal**: Implement data exchange and replication between peers.

- **Tasks**:
  - Define a `Block` class for data blocks with a unique hash identifier.
  - Set up a simple protocol for requesting and sending data blocks between peers.

- **Deliverable**: BitTorrent-like data exchange and replication of blocks.

### Phase 4: Merkledag Layer (Data Structuring)

**Goal**: Build the Merkle DAG structure for content-addressed storage.

- **Tasks**:
  - Implement a `MerkleNode` class with content hashes and child node links.
  - Link nodes based on their cryptographic hashes to create a Merkle DAG.

- **Deliverable**: A basic Merkle DAG system for content-addressed storage and retrieval.

### Phase 5: Naming Layer (IPNS)

**Goal**: Implement a simple IPNS for human-readable, mutable content addressing.

- **Tasks**:
  - Create a `NameRecord` class to link names with the latest hash of content.
  - Use public/private keys for signing and updating name records.

- **Deliverable**: A naming system for resolving human-readable names to hashes.

### Phase 6: Integration & Optimization

**Goal**: Ensure all layers work together seamlessly.

- **Tasks**:
  - Test end-to-end file storage and retrieval.
  - Implement caching and optimize data transfer and routing.
  - Add performance improvements to peer-to-peer communication and DHT lookups.

- **Deliverable**: A cohesive, functional IPFS-like system.

### Erasure Encoding

**Goal**: Add redundancy and fault tolerance to the system by implementing erasure encoding.

- **Where it fits**: Introduce erasure encoding at the `Exchange` and `Merkledag` layers for dividing data into redundant pieces before distributing it across peers.
- **Tasks**:
  - Use Raptor codes or a similar erasure coding technique to split data into multiple chunks with redundancy.
  - Integrate erasure encoding into the data storage and transfer process.

- **Deliverable**: Fault-tolerant file storage with erasure-encoded data chunks distributed across peers.

## How to Run

TO-DO



## Future Enhancements

- **Security**: Add encryption to peer-to-peer communication (TLS/SSL).
- **Routing Optimization**: Implement advanced DHT algorithms like Kademlia for efficient routing.
- **File Deduplication and Compression**: Minimize data redundancy and optimize storage.
- **Erasure Encoding**: Implement erasure coding to ensure data is recoverable even when peers go offline.


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
