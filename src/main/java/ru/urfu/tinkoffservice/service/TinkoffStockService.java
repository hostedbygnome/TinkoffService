package ru.urfu.tinkoffservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse;
import ru.tinkoff.piapi.contract.v1.InstrumentShort;
import ru.tinkoff.piapi.core.InvestApi;
import ru.urfu.tinkoffservice.dto.*;
import ru.urfu.tinkoffservice.exception.StockNotFoundException;
import ru.urfu.tinkoffservice.model.Stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TinkoffStockService implements StockService {
    private final InvestApi investApi;

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<List<InstrumentShort>> getInstrumentByTicker(String ticker) {
        log.info(Thread.currentThread().getName());
        log.info(Thread.currentThread().getThreadGroup().getName());
        var instrumentsService = investApi.getInstrumentsService();
        return instrumentsService.findInstrument(ticker);
    }

    @Override
    public List<Stock> getStockByTicker(String ticker) {
        if (ticker == null || ticker.equals("")) return Collections.emptyList();
        var tickersCf = getInstrumentByTicker(ticker);
        var tickers = tickersCf
                .join()
                .stream()
                .filter(t -> t.getInstrumentType().equals("share")
                        || t.getInstrumentType().equals("bond")
                        || t.getInstrumentType().equals("etfs"))
                .toList();
        if (tickers.isEmpty()) {
            throw new StockNotFoundException(String.format("Stock %s not found.", ticker));
        }
        return tickers.stream().map(
                        item -> investApi.getInstrumentsService()
                                .getInstrumentByFigi(tickers.get(tickers.indexOf(item)).getFigi())
                                .join())
                .filter(item -> {
                    try {
                        AvailableClassCodes.valueOf(item.getExchangeBytes().toStringUtf8());
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }

                })
                .map(i -> new Stock(
                        i.getTicker(),
                        i.getExchangeBytes().toStringUtf8(),
                        i.getCountryOfRiskName(),
                        i.getFigi(),
                        i.getNameBytes().toStringUtf8(),
                        i.getInstrumentType(),
                        i.getCurrency().toUpperCase(),
                        "Tinkoff",
                        getPrice(i.getFigi())))
                .toList();
//        var item = investApi.getInstrumentsService().getInstrumentByFigi(tickers.get(0).getFigi()).join();
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<StocksDto> getStocksByTickers(TickersDto tickers) {
        log.info(Thread.currentThread().getName());
        // Synchronized
        double startTime = System.currentTimeMillis();
        tickers.getTickers().forEach(this::getStockByTicker);
        log.info(String.format("Time %.4f", System.currentTimeMillis() - startTime));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startTime = System.currentTimeMillis();
        List<CompletableFuture<List<InstrumentShort>>> instruments = new ArrayList<>();
        tickers.getTickers()
                .stream()
                .filter(t -> !t.equals(""))
                .forEach(ticker -> instruments.add(getInstrumentByTicker(ticker)));
         List<Stock> stocks = instruments.stream()
                .map(CompletableFuture::join)
                .map(instrument -> {
                    if (instrument.get(0).getInstrumentType().equals("share")
                            || instrument.get(0).getInstrumentType().equals("bond")) {
                        return investApi.getInstrumentsService()
                                .getInstrumentByFigi(instrument.get(0).getFigi())
                                .join();
                    } else return null;
                })
                .filter(Objects::nonNull)
                .map(i -> new Stock(
                        i.getTicker(),
                        i.getExchangeBytes().toStringUtf8(),
                        i.getCountryOfRiskName(),
                        i.getFigi(),
                        i.getNameBytes().toStringUtf8(),
                        i.getInstrumentType(),
                        i.getCurrency().toUpperCase(),
                        "Tinkoff",
                        getPrice(i.getFigi())))
                .toList();
        log.info(String.format("Time %.4f", System.currentTimeMillis() - startTime));
        return CompletableFuture.completedFuture(new StocksDto(stocks));
    }

    public StockPrice getPrice(String figi) {
        log.info(Thread.currentThread().getName());
        var orderBook = investApi.getMarketDataService().getOrderBook(figi, 1).join();
        var currency = investApi.getInstrumentsService()
                .getInstrumentByFigi(figi)
                .join()
                .getCurrency().toUpperCase();
        return orderBook.hasLastPrice() ? new StockPrice(figi, orderBook.getLastPrice().getUnits() +
                String.format(".%d", orderBook.getLastPrice().getNano() % 1000), currency)
                : new StockPrice(figi, "Last price not available", currency);
    }

//    @Async("threadPoolTaskExecutor")
//    public CompletableFuture<GetOrderBookResponse> getOrderBookByFigi(String figi) {
//        var orderBook = investApi.getMarketDataService().getOrderBook(figi, 1);
//        return orderBook;
//    }

    @Override
    public StockPricesDto getPrices(FigiesDto figiesDto) {
        double startTime = System.currentTimeMillis();
        List<StockPrice> prices = new ArrayList<>();
        try(ExecutorService executor = Executors.newFixedThreadPool(6)) {
            List<Callable<StockPrice>> tasks = new ArrayList<>();
            for (int figi = 0; figi < figiesDto.getFigies().size(); figi++) {
                final int currFigi = figi;
                tasks.add(() -> getPrice(figiesDto.getFigies().get(currFigi)));
            }
            List<Future<StockPrice>> futures = executor.invokeAll(tasks);

            for (var future: futures) {
                prices.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        log.info(String.format("Time: %.4f", System.currentTimeMillis() - startTime));
        return new StockPricesDto(prices);
//        List<CompletableFuture<GetOrderBookResponse>> orderBooks = new ArrayList<>();
//        figiesDto.getFigies().forEach(figi -> orderBooks.add(getOrderBookByFigi(figi)));
//        List<StockPrice> stockPrices = orderBooks.stream()
//                .map(CompletableFuture::join)
//                .map(ob -> {
//                    if (ob.hasLastPrice()) {
//                        return new StockPrice(
//                                ob.getFigi(),
//                                ob.getLastPrice().getUnits() +
//                                        String.format(".%d", ob.getLastPrice().getNano() % 1000)
//                        );
//                    } else {
//                        return new StockPrice(
//                                ob.getFigi(),
//                                "0.0");
//                    }
//                })
//                .toList();
//
//        return new StockPricesDto(stockPrices);
    }
}
