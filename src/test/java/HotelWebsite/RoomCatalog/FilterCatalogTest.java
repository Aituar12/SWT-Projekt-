package HotelWebsite.RoomCatalog;

import HotelWebsite.RoomCatalog.Room.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class FilterCatalogTest {

	private static Set<DedicatedRoom> mockSet;

	private static CatalogFilter catalog;

	@BeforeAll
	public static void setUp() {

		mockSet = new HashSet<>();

		CatalogRepository repo = mock(CatalogRepository.class);

		when(repo.save(any(DedicatedRoom.class))).then(invocation -> {
			DedicatedRoom room = invocation.getArgument(0);
			mockSet.add(room);
			return room;
		});

		when(repo.findAllByDedicated(false)).then(invocationOnMock -> {
			Set<DedicatedRoom> result = new HashSet<>();
			for (DedicatedRoom room : mockSet) {
				if(!room.getDedicated()) {
					result.add(room);
				}
			}
			return result;
		});

		catalog = spy(new CatalogFilter(repo));

		//Initialize some equipment
		EquipmentItem tv = new EquipmentItem("TV", 10.0);
		EquipmentItem doubleBed = new EquipmentItem("Queen-sized bed", 23.5);
		EquipmentItem carpet = new EquipmentItem("Carpet", 5.0);
		EquipmentItem kitchen = new EquipmentItem("Kitchen", 1.0);

		//To add the Equipment to the room, it has to be a set
		HashSet<EquipmentItem> equipmentItems = new HashSet<>();
		HashSet<EquipmentItem> carpetSet = new HashSet<>();
		HashSet<EquipmentItem> bedSet = new HashSet<>();
		HashSet<EquipmentItem> kitchenSet = new HashSet<>();

		//Add the Equipment to the empty Set
		equipmentItems.add(tv);
		carpetSet.add(carpet);
		bedSet.add(doubleBed);
		kitchenSet.add(kitchen);

		//Set the id of the tv object cause database not working in a Test
		tv.setId(1L);

		//Initialize some Rooms
		Room zimmer1 = new Room("Romantic Double",25.3, RoomType.DOUBLE, equipmentItems,"sdbr");
		Room zimmer2 = new Room("Spacious Single", 20.8, RoomType.SINGLE, equipmentItems,"asbr");
		Room suiteZimmer1 = new Room("Bedroom 1", 15.0, RoomType.DOUBLE, carpetSet);
		Room suiteZimmer2 = new Room("Master Bedroom", 30.0, RoomType.DOUBLE, bedSet);
		Room suiteZimmer3 = new Room("Living Room", 20.0, RoomType.SUITEROOM, carpetSet);
		Room suiteZimmer4 = new Room("Kitchen", 15.0, RoomType.SUITE, kitchenSet);
		Room zimmer3 = new Room("Room 3", 20.0, RoomType.DOUBLE, new HashSet<>());

		zimmer3.setBookedDates(LocalDate.now(),LocalDate.now().plusDays(1));
		//For a suite there are a Set of rooms required
		Set<Room> suiteRooms = new HashSet<>();
		suiteRooms.add(suiteZimmer1);
		suiteRooms.add(suiteZimmer2);
		suiteRooms.add(suiteZimmer3);
		catalog.addRoom(zimmer3);

		//Initialize the suite
		Suite suite1 = new Suite("Grand Suite",suiteRooms, "");

		//save the rooms to the database/repository

		catalog.addRoom(zimmer1);
		catalog.addRoom(zimmer2);
		//catalog.addRoom(suiteZimmer4);
		catalog.addRoom(suite1);
	}

	@Test
	void filterByDedicatedTest() {
		assertEquals(4,catalog.filterByDedicated(false).size());
	}

	@Test
	void filterCatalogTest() {

		Long[] equipment = {1L};
		RoomType[] type = {RoomType.SINGLE};

		assertEquals(1, catalog.filterCatalog(LocalDate.now(),LocalDate.now().plusDays(2),
			1, type, 1.0, equipment).size());
	}

	@Test
	void filterCatalogAllNullTest() {
		List<DedicatedRoom> result = new ArrayList<>(catalog.filterCatalog(null, null, 0,
			null, 0.0, null));
		assertEquals(4,result.size());
	}
}
