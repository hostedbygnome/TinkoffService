package ru.urfu.tinkoffservice.dto;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Value;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.math.BigDecimal;

@AllArgsConstructor
@Value
public class StockPrice {
    String figi;
    String price;
    String currency;
}
