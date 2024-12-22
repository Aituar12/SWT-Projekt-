package HotelWebsite.RoomCatalog;

import HotelWebsite.RoomCatalog.Room.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class DedicatedRoomTest {

	private static List<DedicatedRoom> rooms;

	@BeforeAll
	static void setUp() {

		EquipmentItem tv = new EquipmentItem("TV",10.0);
		EquipmentItem doubleBed = new EquipmentItem("Double bed",25.0);
		EquipmentItem pool = new EquipmentItem("Pool", 100.0);

		Set<EquipmentItem> tvAndDouble = new HashSet<>();
		tvAndDouble.add(tv);
		tvAndDouble.add(doubleBed);

		Set<EquipmentItem> poolSet = new HashSet<>();
		poolSet.add(pool);

		Set<EquipmentItem> everything = new HashSet<>(tvAndDouble);
		everything.add(pool);

		Set<EquipmentItem> nothing = new HashSet<>();

		Room discreteRoom1 = new Room("Discrete Room 1", 25.0, RoomType.DOUBLE, tvAndDouble, "abc" );
		discreteRoom1.setBookedDates(LocalDate.now(), LocalDate.now().plusDays(3));

		Room dedicatedRoom1 = new Room("Dedicated Room1", 10.0, RoomType.SUITEROOM, poolSet);
		Room dedicatedRoom2 = new Room("Dedicated Room2", 123.0, RoomType.SINGLE, everything);

		Set<Room> suiteRooms = new HashSet<>();
		suiteRooms.add(dedicatedRoom1);
		suiteRooms.add(dedicatedRoom2);

		Suite suite = new Suite("Suite", suiteRooms);

		rooms = new ArrayList<>();
		rooms.add(0, discreteRoom1);
		rooms.add(1, suite);
		rooms.add(2, dedicatedRoom1);
		rooms.add(3, dedicatedRoom2);
	}

	@Test
	void roomTypeSuiteSetsRoomsToSuiteRoomsTest() {
		Room suiteRoom = new Room("suiteRoom", 20.0, RoomType.SUITE, new HashSet<>());
		assertEquals(RoomType.SUITEROOM, suiteRoom.getRoomType());
	}

	@Test
	void roomsAreNotDedicatedTest() {
		assertEquals(false, rooms.get(0).getDedicated());
	}

	@Test
	void suiteRoomsAreDedicated() {
		assertEquals(true, rooms.get(3).getDedicated());
	}

	@Test
	void setBookedDatesTest() {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = LocalDate.now().plusDays(1);
		rooms.get(1).setBookedDates(startDate, endDate);
		assertTrue(rooms.get(1).getBookedDates().contains(LocalDate.now()));
	}

	@Test
	void removeBookedDatesTest() {
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = LocalDate.now().plusDays(1);
		rooms.get(0).removeBookedDates(startDate, endDate);
		assertFalse(rooms.get(0).getBookedDates().contains(LocalDate.now()));
	}
}
