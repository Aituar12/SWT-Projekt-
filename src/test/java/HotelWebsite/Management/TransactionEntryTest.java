package HotelWebsite.Management;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionEntryTest {

	private static TransactionEntry entry;

	@BeforeAll
	public static void setUp(){
		entry = new TransactionEntry(20, LocalDate.now());
	}

	@Test
	public void getDateTest(){

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM uuuu");
		String expected = LocalDate.now().format(formatter);
		System.out.println(entry.getDate());
		assertEquals(expected,entry.getDate());
	}

	@Test
	public void equalsTest(){
		Object o = new Object();
		assert(entry.equals(entry));
		assertFalse(entry.equals(o));
	}
}
