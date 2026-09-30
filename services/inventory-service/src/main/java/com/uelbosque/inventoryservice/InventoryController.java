package com.uelbosque.inventoryservice;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
@RestController @RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service=service; }
    @GetMapping public List<StockResponse> list() { return service.list(); }
    @GetMapping("/{code}") public StockResponse get(@PathVariable String code) { return service.get(code); }
    @PutMapping("/{code}")
    public StockResponse adjust(@PathVariable String code,@Valid @RequestBody Adjustment request) { return service.adjust(code,request.onHand()); }
    @PostMapping("/reservations")
    public ReservationResponse reserve(@Valid @RequestBody ReservationRequest request) { return service.reserve(request); }
    @PostMapping("/reservations/{key}/commit")
    public ReservationResponse commit(@PathVariable String key) { return service.finish(key,true); }
    @PostMapping("/reservations/{key}/release")
    public ReservationResponse release(@PathVariable String key) { return service.finish(key,false); }
    public record Adjustment(@NotNull @Min(0) @Max(1000000000) Integer onHand) {}
}
