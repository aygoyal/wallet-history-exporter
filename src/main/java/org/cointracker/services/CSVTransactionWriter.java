package org.cointracker.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.cointracker.entities.Transaction;
import org.cointracker.interfaces.TransactionWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class CSVTransactionWriter implements TransactionWriter, AutoCloseable {
    private static final String[] HEADERS = new String[] {
            "Transaction Hash",
            "Date & Time",
            "From Address",
            "To Address",
            "Transaction Type",
            "Asset Contract Address",
            "Asset Symbol / Name",
            "Token ID",
            "Value / Amount",
            "Gas Fee"
    };

    private Path filePath;
    private CSVPrinter printer;

    @Override
    public void open(String address) throws IOException {
        String filename = address + ".csv";
        filePath = Path.of(filename);

        printer = new CSVPrinter(
                new FileWriter(filePath.toFile()),
                CSVFormat.DEFAULT.builder().setHeader(HEADERS).build()
        );
    }

    public void writeTransaction(Transaction transaction) {
        try {
            printer.printRecord(
                    transaction.getTransactionHash(),
                    Date.from(transaction.getDateTime()),
                    "'" + transaction.getFromAddress(),
                    "'" + transaction.getToAddress(),
                    transaction.getType().getName(),
                    transaction.getAssetContractAddress(),
                    transaction.getAssetSymbolName(),
                    transaction.getTokenID(),
                    transaction.getValue(),
                    transaction.getGasFee()
            );
        } catch (Exception ex) {
            System.out.println("Failed to write transaction: " + transaction);
        }
    }

    @Override
    public void close() throws IOException {
        printer.flush();
        printer.close();
    }

    @Override
    public void abort() throws IOException {
        if (filePath != null) {
            Files.deleteIfExists(filePath);
        }
    }
}
