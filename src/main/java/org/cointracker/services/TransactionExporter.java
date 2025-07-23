package org.cointracker.services;

import org.cointracker.entities.Transaction;
import org.cointracker.interfaces.TransactionClient;
import org.cointracker.interfaces.TransactionWriter;

import java.io.IOException;
import java.util.List;

public class TransactionExporter {
    private final TransactionClient client;
    private final TransactionWriter writer;

    public TransactionExporter(TransactionClient client, TransactionWriter writer) {
        this.client = client;
        this.writer = writer;

    }

    public void exportAll(String address) throws IOException {
        try {
            writer.open(address);

            client.streamExternalTransactions(address, txPage -> writePage(writer, txPage));
            client.streamInternalTransactions(address, txPage -> writePage(writer, txPage));
            client.streamTokenTransactions(address, txPage -> writePage(writer, txPage));
            client.streamNFTTransactions(address, txPage -> writePage(writer, txPage));
        } catch (Exception e) {
            System.err.println("Export Failed: " + e.getMessage());
            e.printStackTrace();

            try {
                writer.abort();
                System.out.println("Partial output cleaned up.");
            } catch (Exception ex) {
                System.err.println("Could not clean up partial output: " + ex.getMessage());
            }
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                System.err.println("Failed to close writer: " + ex.getMessage());
            }
        }
    }

    private void writePage(TransactionWriter writer, List<Transaction> txPage) {
        for (Transaction tx : txPage) writer.writeTransaction(tx);
    }
}
