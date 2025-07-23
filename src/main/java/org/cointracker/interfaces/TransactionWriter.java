package org.cointracker.interfaces;

import org.cointracker.entities.Transaction;

import java.io.IOException;

public interface TransactionWriter {
    void open(String address) throws IOException;
    void writeTransaction(Transaction tx);
    void close() throws IOException;
    // perform cleanup tasks
    void abort() throws IOException;
}
