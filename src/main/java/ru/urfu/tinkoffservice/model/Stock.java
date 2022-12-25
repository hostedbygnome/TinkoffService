package ru.urfu.tinkoffservice.model;

import lombok.AllArgsConstructor;
import lombok.Value;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.urfu.tinkoffservice.dto.StockPrice;

@Value
@AllArgsConstructor
public class Stock {
    String ticker;
    String exchange;
    String countryOfRiskName;
    String fiji;
    String name;
    String type;
    String currency;
    String source;
    StockPrice price;
}
