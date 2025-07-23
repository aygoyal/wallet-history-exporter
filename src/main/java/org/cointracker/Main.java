package org.cointracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cointracker.interfaces.TransactionClient;
import org.cointracker.interfaces.TransactionWriter;
import org.cointracker.services.CSVTransactionWriter;
import org.cointracker.services.EtherscanTransactionClient;
import org.cointracker.services.TransactionExporter;

import java.io.IOException;
import java.net.http.HttpClient;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar target/wallet-history-exporter-1.0-SNAPSHOT.jar <ETH_ADDRESS> <ETHERSCAN_API_KEY>");
            System.exit(1);
        }

        String address = args[0];
        String apiKey = args[1];

        try {
            TransactionClient client = new EtherscanTransactionClient(apiKey, HttpClient.newHttpClient(), new ObjectMapper());
            TransactionWriter writer = new CSVTransactionWriter();
            TransactionExporter exporter = new TransactionExporter(client, writer);
            exporter.exportAll(address);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}