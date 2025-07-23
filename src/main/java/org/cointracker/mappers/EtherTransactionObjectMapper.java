package org.cointracker.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import org.cointracker.entities.Transaction;
import org.cointracker.entities.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class EtherTransactionObjectMapper {

    public static Transaction fromJson(JsonNode json, TransactionType type) {
        String valueRaw = json.path("value").asText();
        String tokenDecimal = json.has("tokenDecimal") ? json.get("tokenDecimal").asText() : "";

        BigDecimal value = calculateValue(type, valueRaw, type == TransactionType.ERC_20 ? tokenDecimal : null);

        String tokenID = json.has("tokenID") ? json.get("tokenID").asText() : "";

        return new Transaction.Builder(
                json.path("hash").asText(),
                Instant.ofEpochSecond(json.path("timeStamp").asLong()),
                json.path("from").asText(),
                json.path("to").asText(),
                type,
                value,
                calculateGasFee(json)
        )
        .withAssetContractAddress(json.path("contractAddress").asText(""))
        .withAssetSymbolName(json.path("tokenSymbol").asText(""))
        .withTokenID(tokenID)
        .build();
    }

    private static BigDecimal calculateGasFee(JsonNode node) {
        String gasPrice = node.path("gasPrice").asText("0");
        String gasUsed = node.path("gasUsed").asText("0");

        if (!gasPrice.equals("0") && !gasUsed.equals("0")) {
            return new BigDecimal(gasPrice)
                    .multiply(new BigDecimal(gasUsed))
                    .divide(BigDecimal.TEN.pow(18), RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private static BigDecimal calculateValue(TransactionType type, String valueRaw, String tokenDecimalOrId) {
        if (valueRaw == null || valueRaw.isBlank()) {
            return BigDecimal.ZERO;
        }

        switch (type) {
            case EXTERNAL:
            case INTERNAL:
                return new BigDecimal(valueRaw)
                        .divide(BigDecimal.TEN.pow(18), RoundingMode.HALF_UP);
            case ERC_20:
                int decimals = 0;
                if (tokenDecimalOrId != null && !tokenDecimalOrId.isBlank()) {
                    try {
                        decimals = Integer.parseInt(tokenDecimalOrId);
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid tokenDecimal: " + tokenDecimalOrId);
                    }
                }
                return new BigDecimal(valueRaw)
                        .movePointLeft(decimals);
            case ERC_721:
                return BigDecimal.ONE;
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }
    }
}
