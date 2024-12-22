package HotelWebsite.order;


import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.salespointframework.order.Order.OrderIdentifier;

import HotelWebsite.RoomCatalog.Room.DedicatedRoom;
import HotelWebsite.RoomCatalog.Room.EquipmentItem;
import HotelWebsite.RoomCatalog.Room.Room;
import HotelWebsite.RoomCatalog.Room.RoomType;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.money.Monetary;


public class ReservationTest {

    private static Reservation testReservation;
    private static Set<DedicatedRoom> testRoomSet;

	@BeforeAll
	public static void setUp(){
        testRoomSet = new HashSet<>();
        Room testRoom1 = new Room("Suite", 42, RoomType.SUITE, new HashSet<EquipmentItem>());
        testRoom1.setPrice(Money.of(10, "EUR"));
        testRoomSet.add(testRoom1);
        testReservation = new Reservation("testReservation", Money.of(123, Monetary.getCurrency("EUR")), testRoomSet);
        testReservation.setStartDate(LocalDate.of(1989, 01, 13));
        testReservation.setEndDate(LocalDate.of(1989, 01, 15));
        testReservation.setOrderID(OrderIdentifier.of("123"));
        testReservation.setReservationNumber(Long.valueOf(12));
        testReservation.setPaymentStatus(PaymentStatus.OPEN);
	}

	@Test
    @Order(1)
	public void testGetRoomSet(){
        assertEquals(testRoomSet, testReservation.getRoomSet(), "Sets are not equal");
    }

    @Test
    @Order(2)
    public void testAddRoom() {
        assertEquals(1, testReservation.getRoomSet().size(), "Not Equal");
        Room testRoom2 = new Room("Suite", 40, RoomType.SUITE, new HashSet<EquipmentItem>());
        testRoom2.setPrice(Money.of(30, "EUR"));
        testReservation.addRoom(testRoom2);
        assertEquals(2, testReservation.getRoomSet().size(), "Not Equal after adding Room");
    }

    @Test
    @Order(3)
    public void testUpdatePrice() {
        testReservation.updatePrice();
        assertEquals(Money.of(80, Monetary.getCurrency("EUR")), testReservation.getPrice());
    }

    @Test
    @Order(4)
	public void testGetStartDate(){
        assertEquals(LocalDate.of(1989, 01, 13), testReservation.getStartDate(), "StartDate does not match actual StartDate");
	}

    @Test
    @Order(5)
    public void testGetEndDate(){
        assertEquals(LocalDate.of(1989, 01, 15), testReservation.getEndDate(), "EndDate does not match actual EndDate");
	}

    @Test
    @Order(6)
    public void testGetOrderId() {
        assertEquals(OrderIdentifier.of("123"), testReservation.getOrderId());
    }

    @Test
    @Order(7)
    public void testGetTimeStatus() {
        assertEquals(TimeStatus.EXPIRED, testReservation.getTimeStatus());
    }

    @Test
    @Order(8)
    public void testGetDiffDays() {
        assertEquals(2, testReservation.getDiffDays());
    }

    @Test
    @Order(9)
    public void testGetReservationNumber() {
        assertEquals(Long.valueOf(12), testReservation.getReservationNumber());
    }

    @Test
    @Order(10)
    public void testGetPaymentStatus() {
        assertEquals(PaymentStatus.OPEN, testReservation.getPaymentStatus());
    }

    @Test
    @Order(11)
    public void testSetReservationNumber() {
        Reservation testReservationNew = new Reservation();
        testReservationNew.setReservationNumber(Long.valueOf(123));
        assertEquals(Long.valueOf(123), testReservationNew.getReservationNumber());
    }

    @Test
    @Order(12)
    public void testSetOrderId() {
        testReservation.setOrderID(OrderIdentifier.of("1234"));
        assertEquals(OrderIdentifier.of("1234"), testReservation.getOrderId());
    }

    @Test
    @Order(13)
    public void testSetPaymentStatus() {
        testReservation.setPaymentStatus(PaymentStatus.CANCELED);
        assertEquals(PaymentStatus.CANCELED, testReservation.getPaymentStatus());
    }

    @Test
    @Order(14)
	public void testSetStartDate(){
        testReservation.setStartDate(LocalDate.of(1989, 01, 15));
        assertEquals(LocalDate.of(1989, 01, 15), testReservation.getStartDate(), "StartDate does not match actual StartDate");
	}

    @Test
    @Order(15)
	public void testSetEndDate(){
        testReservation.setEndDate(LocalDate.of(1989, 01, 17));
        assertEquals(LocalDate.of(1989, 01, 17), testReservation.getEndDate(), "EndDate does not match actual EndDate");
	}
}

