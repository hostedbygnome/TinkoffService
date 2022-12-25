package ru.urfu.tinkoffservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.urfu.tinkoffservice.dto.FigiesDto;
import ru.urfu.tinkoffservice.dto.StockPricesDto;
import ru.urfu.tinkoffservice.dto.StocksDto;
import ru.urfu.tinkoffservice.dto.TickersDto;
import ru.urfu.tinkoffservice.model.Stock;
import ru.urfu.tinkoffservice.service.StockService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;
    @GetMapping("/stocks/{ticker}")
    public List<Stock> getStock(@PathVariable String ticker) {
        return stockService.getStockByTicker(ticker);
    }

    @PostMapping("/stocks/getStocksByTickers")
    public StocksDto getStocksByTickers(@RequestBody TickersDto tickersDto) {
        return stockService.getStocksByTickers(tickersDto).join();
    }

    @PostMapping("/prices")
    public StockPricesDto getPrices(@RequestBody FigiesDto figiesDto) {
        return stockService.getPrices(figiesDto);
    }
}
