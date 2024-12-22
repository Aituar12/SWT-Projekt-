package HotelWebsite.order;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;



public class DateHolderTest {

    private static DateHolder testDateHolder;

	@BeforeAll
	public static void setUp(){
        testDateHolder = new DateHolder();
        testDateHolder.setStartDate(LocalDate.of(1989, 01, 13));
        testDateHolder.setEndDate(LocalDate.of(1989, 01, 14));
	}

	@Test
    @Order(1)
	public void testGetStartDate(){
        assertEquals(LocalDate.of(1989, 01, 13), testDateHolder.getStartDate(), "StartDate does not match actual StartDate");
	}

    @Test
    @Order(2)
    public void testGetEndDate(){
        assertEquals(LocalDate.of(1989, 01, 14), testDateHolder.getEndDate(), "EndDate does not match actual EndDate");
	}

    @Test
    @Order(3)
	public void testSetStartDate(){
        testDateHolder.setStartDate(LocalDate.of(1989, 01, 15));
        assertEquals(LocalDate.of(1989, 01, 15), testDateHolder.getStartDate(), "StartDate does not match actual StartDate");
	}

    @Test
    @Order(4)
	public void testSetEndDate(){
        testDateHolder.setEndDate(LocalDate.of(1989, 01, 17));
        assertEquals(LocalDate.of(1989, 01, 17), testDateHolder.getEndDate(), "EndDate does not match actual EndDate");
	}
}
