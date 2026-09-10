package com.uelbosque.inventoryservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
@Service @Transactional
public class InventoryService {
    private final StockRepository stocks;
    private final ReservationRepository reservations;
    public InventoryService(StockRepository stocks,ReservationRepository reservations) { this.stocks=stocks; this.reservations=reservations; }
    public StockResponse adjust(String code,int onHand) {
        if (onHand<0) throw new IllegalArgumentException("Cantidad negativa");
        Stock stock=stocks.locked(code).orElseGet(() -> new Stock(code,0));
        stock.adjust(onHand);
        return stocks.saveAndFlush(stock).response();
    }
    @Transactional(readOnly=true)
    public List<StockResponse> list() { return stocks.findAll().stream().map(Stock::response).toList(); }
    @Transactional(readOnly=true)
    public StockResponse get(String code) {
        return stocks.findById(code).orElseThrow(() -> missing()).response();
    }
    public ReservationResponse reserve(ReservationRequest request) {
        Stock stock=stocks.locked(request.productCode()).orElseThrow(() -> missing());
        var prior=reservations.findById(request.key());
        if (prior.isPresent()) return repeated(prior.get(),request);
        stock.reserve(request.quantity());
        return reservations.saveAndFlush(new Reservation(request)).response();
    }
    private ReservationResponse repeated(Reservation prior,ReservationRequest request) {
        if (!prior.matches(request)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Clave usada por otra reserva");
        return prior.response();
    }
    public ReservationResponse finish(String key,boolean commit) {
        Reservation reservation=reservations.locked(key).orElseThrow(() -> missing());
        String target=commit ? "COMMITTED" : "RELEASED";
        if (target.equals(reservation.getStatus())) return reservation.response();
        if (!"RESERVED".equals(reservation.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Reserva finalizada");
        Stock stock=stocks.locked(reservation.getProductCode()).orElseThrow(() -> missing());
        if (commit) stock.commit(reservation.getQuantity()); else stock.release(reservation.getQuantity());
        reservation.complete(target);
        return reservation.response();
    }
    private ResponseStatusException missing() { return new ResponseStatusException(HttpStatus.NOT_FOUND,"Existencia o reserva inexistente"); }
}
