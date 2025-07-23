package org.cointracker.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cointracker.entities.Transaction;
import org.cointracker.entities.TransactionType;
import org.cointracker.interfaces.TransactionClient;
import org.cointracker.mappers.EtherTransactionObjectMapper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EtherscanTransactionClient implements TransactionClient {

    private static final String ONE = "1";
    private static final String NO_TRANSACTIONS_FOUND = "No transactions found";

    private static final String BASE_URL = "https://api.etherscan.io/api";
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EtherscanTransactionClient(String apiKey, HttpClient httpClient, ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void streamExternalTransactions(String address, Consumer<List<Transaction>> consumer) {
        stream(address, "txlist", TransactionType.EXTERNAL, consumer);
    }

    @Override
    public void streamInternalTransactions(String address, Consumer<List<Transaction>> consumer) {
        stream(address, "txlistinternal", TransactionType.INTERNAL, consumer);
    }

    @Override
    public void streamTokenTransactions(String address, Consumer<List<Transaction>> consumer) {
        stream(address, "tokentx", TransactionType.ERC_20, consumer);
    }

    @Override
    public void streamNFTTransactions(String address, Consumer<List<Transaction>> consumer) {
        stream(address, "tokennfttx", TransactionType.ERC_721, consumer);
    }

    private void stream(String address, String action, TransactionType type, Consumer<List<Transaction>> consumer) {
        BigInteger startBlock = BigInteger.ZERO;
        int pageSize = 10_000;
        boolean hasNext = true;

        System.out.println("Starting streaming of [" + type.getName() + "] transactions for address [" + address + "]");

        while (hasNext) {
            waitIfNeeded();

            String url = String.format(
                    "%s?module=account&action=%s&address=%s&startblock=%s&endblock=99999999&offset=%d&sort=asc&apikey=%s",
                    BASE_URL, action, address, startBlock, pageSize, apiKey
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            try {
                System.out.println("Sending request to get [" + type.getName() + "] transactions for address: [" + address + "]");
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Unexpected response code: " + response.statusCode());
                }

                JsonNode json = objectMapper.readTree(response.body());
                String status = json.path("status").asText();

                if (!ONE.equals(status)) {
                    String message = json.path("message").asText();
                    if (NO_TRANSACTIONS_FOUND.equalsIgnoreCase(message)) {
                        System.out.println("No transactions found for address [" + address + "]");
                        break;
                    } else {
                        throw new RuntimeException("Etherscan API error: " + message);
                    }
                }

                JsonNode result = json.path("result");
                if (!result.isArray() || result.isEmpty()) {
                    hasNext = false;
                } else {
                    List<Transaction> transactions = new ArrayList<>();
                    BigInteger maxBlockInPage = startBlock;

                    System.out.println("Mapping response to transactions...");

                    for (JsonNode transactionJson : result) {
                        Transaction transaction = EtherTransactionObjectMapper.fromJson(transactionJson, type);
                        transactions.add(transaction);

                        BigInteger blockNumber = new BigInteger(transactionJson.path("blockNumber").asText());
                        if (blockNumber.compareTo(maxBlockInPage) > 0) {
                            maxBlockInPage = blockNumber;
                        }
                    }

                    consumer.accept(transactions);

                    if (result.size() < pageSize) {
                        hasNext = false;
                    } else {
                        startBlock = maxBlockInPage.add(BigInteger.ONE);
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Error in fetching transactions for block " + startBlock);
            }
        }

        System.out.println("All [" + type.getName() + "] transactions for address [" + address + "] fetched successfully...");
    }

    private void waitIfNeeded() {
        try {
            Thread.sleep(500);  // ~2 request/sec
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
