package org.cointracker.entities;

public enum TransactionType {
    EXTERNAL("External"), INTERNAL("Internal"), ERC_20("ERC-20"), ERC_721("ERC-721");

    private final String name;

    TransactionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
