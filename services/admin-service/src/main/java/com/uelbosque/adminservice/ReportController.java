package com.uelbosque.adminservice;

import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping({"/api/reports","/api/admin/reports"})
public class ReportController {
    private final ReportService service;
    public ReportController(ReportService service) { this.service=service; }
    @GetMapping public List<String> available() { return List.of("users","customers","sales-by-customer"); }
    @GetMapping("/users") public List<ReportClient.UserRow> users() { return service.users(); }
    @GetMapping("/customers") public List<ReportClient.CustomerRow> customers() { return service.customers(); }
    @GetMapping("/sales-by-customer") public SalesReport sales() { return service.salesByCustomer(); }
}
