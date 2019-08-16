package io.pivotal.marketdemo.writer;

import com.amazonaws.util.StringInputStream;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WriterTaskTests {
    @Test
    public void testParseInput() throws Exception {
        StringInputStream sis = new StringInputStream("MSFT,100\nAAPL,200");
        WriterConfiguration.WriterTask task = new WriterConfiguration.WriterTask();
        List<ClosePrice> result = task.parseInput(sis, "MSFT");
        assertEquals(1, result.size());
        assertEquals("MSFT", result.get(0).getTicker());
        assertEquals(100.0d, result.get(0).getClose(), 0.01);

        sis = new StringInputStream("MSFT,100\nAAPL,200");
        result = task.parseInput(sis, "MSFT AAPL");
        assertEquals(2, result.size());
        assertEquals("MSFT", result.get(0).getTicker());
        assertEquals(100.0d, result.get(0).getClose(), 0.01);
        assertEquals("AAPL", result.get(1).getTicker());
        assertEquals(200.0d, result.get(1).getClose(), 0.01);

        sis = new StringInputStream("MSFT,100\nAAPL,200");
        result = task.parseInput(sis, "");
        assertEquals(0, result.size());

        sis = new StringInputStream("MSFT,100\nAAPL,200");
        result = task.parseInput(sis, null);
        assertEquals(0, result.size());
    }

    @Test
    public void testCreateJson() throws Exception {
        WriterConfiguration.WriterTask task = new WriterConfiguration.WriterTask();

        List<ClosePrice> prices = new ArrayList<>();
        prices.add(new ClosePrice("MSFT", 100.0));
        assertEquals("[{\"ticker\":\"MSFT\",\"close\":100.0}]", task.createJSON(prices));

        prices.clear();
        prices.add(new ClosePrice("AAPL", 200.0));
        prices.add(new ClosePrice("MSFT", 100.0));
        assertEquals("[{\"ticker\":\"AAPL\",\"close\":200.0},{\"ticker\":\"MSFT\",\"close\":100.0}]", task.createJSON(prices));

    }
}
