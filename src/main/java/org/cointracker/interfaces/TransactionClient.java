package org.cointracker.interfaces;

import org.cointracker.entities.Transaction;

import java.util.List;
import java.util.function.Consumer;

public interface TransactionClient {
    void streamExternalTransactions(String address, Consumer<List<Transaction>> consumer);
    void streamInternalTransactions(String address, Consumer<List<Transaction>> consumer);
    void streamTokenTransactions(String address, Consumer<List<Transaction>> consumer);
    void streamNFTTransactions(String address, Consumer<List<Transaction>> consumer);
}
