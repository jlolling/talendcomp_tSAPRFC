package de.jlo.talendcomp.sap.impl;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import de.jlo.talendcomp.sap.TypeUtil;

public class TestTypeUtilConcurrency {

	@Test
	public void testParallelConversions() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(8);
		List<Future<String>> results = new ArrayList<>();
		for (int t = 0; t < 8; t++) {
			final boolean bigDecimal = (t % 2 == 0);
			results.add(executor.submit(() -> {
				for (int i = 0; i < 20000; i++) {
					String value = i + ".25";
					if (bigDecimal) {
						Object result = TypeUtil.convertToBigDecimal(value);
						if (new BigDecimal(value).equals(result) == false) {
							return "BigDecimal expected: " + value + " got: " + result;
						}
					} else {
						Double result = TypeUtil.convertToDouble(value);
						if (result == null || result.doubleValue() != Double.parseDouble(value)) {
							return "Double expected: " + value + " got: " + result;
						}
					}
				}
				return null;
			}));
		}
		executor.shutdown();
		for (Future<String> f : results) {
			String error = f.get();
			assertTrue(error, error == null);
		}
	}

}
