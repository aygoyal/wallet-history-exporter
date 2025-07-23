Cointracker - Wallet History Exporter

This project fetches and exports the complete transaction history of an Ethereum wallet to a structured CSV file.

Different kinds of transactions that it fetches:
1. External(Normal) Transfers - These are direct transfers between user controlled addresses
2. Internal Transfers - These are transfers that occur within smart contracts & not directly initiated by users.  
3. Token Transfers - ERC-20, ERC-721

Output:

- The CSV includes the following fields for ETH transfers, ERC-20 tokens, ERC-721 NFTs, and ERC-1155 assets:
    - **Transaction Hash** – Unique identifier for the transaction
    - **Date & Time** – Transaction confirmation timestamp
    - **From Address** – Sender's Ethereum address
    - **To Address** – Recipient's Ethereum address or contract
    - **Transaction Type** – ETH transfer, ERC-20, ERC-721, ERC-1155, or contract interaction
    - **Asset Contract Address** – Contract address of the token or NFT (if applicable)
    - **Asset Symbol / Name** – Token symbol (e.g., ETH, USDC) or NFT collection name
    - **Token ID** – Unique identifier for NFTs (ERC-721, ERC-1155)
    - **Value / Amount** – Quantity of ETH or tokens transferred
    - **Gas Fee (ETH)** – Total transaction gas cost

**Setup & Run**

**Prerequisites**
- Java 18+
- Maven 3.8+
- Internet connection (to call Etherscan API)
- Etherscan API Key

**Clone & build**
git clone aygoyal/wallet-history-exporter
cd wallet-history-exporter
mvn clean package

**Run**

java -jar target/wallet-history-exporter-1.0-SNAPSHOT-shaded.jar <ETH_ADDRESS> <ETHERSCAN_API_KEY>

To run directly from IDE:

Run Main.java
Provide two program arguments: <ETH_ADDRESS> <API_KEY>

**Assumptions:**
1. The tool assumes that paginating by startBlock is acceptable, even though it can result in redundant scans if the blockchain changes during execution. A cursor‑based approach would be more efficient but is not supported by Etherscan.
2. The amount field is computed differently based on transaction type:
      ETH: value in wei → ETH
      ERC‑20: value / 10^decimals
      ERC‑721: always 1
3. The Etherscan API limits are 5 requests/sec and 100k/day. The code includes a rate limiter allowing 2 requests/sec to stay within safe limits.
    Daily limit is not incorporated.
4. NFT transactions (ERC‑1155) are treated similarly to ERC‑721 for simplicity.

**Architecture Decisions:**
1. Instead of loading all transactions into memory, the TransactionClient streams paginated results to a Consumer<List<Transaction>> — enabling large wallets to be processed page‑by‑page and written to CSV incrementally.
2. The CSV file is written incrementally, and if any failure occurs midway, the partial file is deleted to prevent corrupt output.
3. For keeping the code extensible, TransactionClient and TransactionWriter interfaces are added so that we can support other transaction APIs like Alchemy , Blockscout, Infura etc and also write the output on other types of output destinations.