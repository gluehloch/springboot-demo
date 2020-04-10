package de.winkler.springboot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RestdemoApplicationTests {

	@Test
	public void contextLoads(TestReporter testReporter) {
		testReporter.publishEntry("This is the new test reporter.");
	}

}
