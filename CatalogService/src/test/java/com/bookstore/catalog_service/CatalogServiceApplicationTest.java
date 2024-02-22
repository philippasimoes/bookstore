package com.bookstore.catalog_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
		locations = "classpath:application-test.properties")
public class CatalogServiceApplicationTest {

	@Test
	void whenContextIsLoaded_thenNoExceptions() {
	}

}
