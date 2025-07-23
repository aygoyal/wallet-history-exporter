package org.cointracker.entities;

import java.math.BigDecimal;
import java.time.Instant;

public class Transaction {
    private final String transactionHash;
    private final Instant dateTime;
    private final String fromAddress;
    private final String toAddress;
    private final TransactionType type;
    private final BigDecimal value;
    private final BigDecimal gasFee;

    // Contract address of the token or NFT
    private String assetContractAddress;
    // Token symbol (e.g., ETH, USDC) or NFT collection name
    private String assetSymbolName;
    // Unique identifier for NFTs (ERC-721, ERC-1155)
    private String tokenID;

    public Transaction(Builder builder) {
        this.transactionHash = builder.transactionHash;
        this.dateTime = builder.dateTime;
        this.fromAddress = builder.fromAddress;
        this.toAddress = builder.toAddress;
        this.type = builder.type;
        this.value = builder.value;
        this.gasFee = builder.gasFee;
        this.assetContractAddress = builder.assetContractAddress;
        this.assetSymbolName = builder.assetSymbolName;
        this.tokenID = builder.tokenID;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimal getGasFee() {
        return gasFee;
    }

    public String getAssetContractAddress() {
        return assetContractAddress;
    }

    public String getAssetSymbolName() {
        return assetSymbolName;
    }

    public String getTokenID() {
        return tokenID;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionHash='" + transactionHash + '\'' +
                ", dateTime=" + dateTime +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", type=" + type +
                ", value=" + value +
                ", gasFee=" + gasFee +
                ", assetContractAddress='" + assetContractAddress + '\'' +
                ", assetSymbolName='" + assetSymbolName + '\'' +
                ", tokenID='" + tokenID + '\'' +
                '}';
    }

    public static class Builder {
        private final String transactionHash;
        private final Instant dateTime;
        private final String fromAddress;
        private final String toAddress;
        private final TransactionType type;
        private final BigDecimal value;
        private final BigDecimal gasFee;

        private String assetContractAddress;
        private String assetSymbolName;
        private String tokenID;

        public Builder(String transactionHash, Instant dateTime, String fromAddress, String toAddress, TransactionType type, BigDecimal value, BigDecimal gasFee) {
            this.transactionHash = transactionHash;
            this.dateTime = dateTime;
            this.fromAddress = fromAddress;
            this.toAddress = toAddress;
            this.type = type;
            this.value = value;
            this.gasFee = gasFee;
        }

        public Builder withAssetContractAddress(String assetContractAddress) {
            this.assetContractAddress = assetContractAddress;
            return this;
        }

        public Builder withAssetSymbolName(String assetSymbolName) {
            this.assetSymbolName = assetSymbolName;
            return this;
        }

        public Builder withTokenID(String tokenID) {
            this.tokenID = tokenID;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}
