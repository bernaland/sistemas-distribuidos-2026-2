package com.uelbosque.catalogservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import com.uelbosque.catalogservice.repository.ProductRepository;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:csv-concurrent;DB_CLOSE_DELAY=-1")
class CsvConcurrencyTest {
    @Autowired ProductImportService importer;
    @Autowired ProductRepository products;
    @MockBean SupplierDirectory suppliers;

    @Test void simultaneousReplacementsNeverMergeFiles() throws Exception {
        var start=new CountDownLatch(1);
        var pool=Executors.newFixedThreadPool(2);
        try {
            var first=pool.submit(() -> load(start,"1,Uno,123,10,0,20"));
            var second=pool.submit(() -> load(start,"2,Dos,123,10,5,20"));
            start.countDown();
            assertEquals(1,first.get(10,TimeUnit.SECONDS));
            assertEquals(1,second.get(10,TimeUnit.SECONDS));
            assertEquals(1,products.count());
        } finally { pool.shutdownNow(); }
    }
    private int load(CountDownLatch start,String csv) throws Exception {
        start.await();
        return importer.replace(new MockMultipartFile("file","productos.csv","text/csv",csv.getBytes()));
    }
}
