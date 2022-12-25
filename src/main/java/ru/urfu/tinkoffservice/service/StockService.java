package ru.urfu.tinkoffservice.service;

import ru.urfu.tinkoffservice.dto.FigiesDto;
import ru.urfu.tinkoffservice.dto.StockPricesDto;
import ru.urfu.tinkoffservice.dto.StocksDto;
import ru.urfu.tinkoffservice.dto.TickersDto;
import ru.urfu.tinkoffservice.model.Stock;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface StockService {
    List<Stock> getStockByTicker(String ticker);
    CompletableFuture<StocksDto> getStocksByTickers(TickersDto tickersDto);
    StockPricesDto getPrices(FigiesDto figiesDto);
}
