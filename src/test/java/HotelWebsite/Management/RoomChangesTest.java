package HotelWebsite.Management;

import HotelWebsite.RoomCatalog.CatalogRepository;
import HotelWebsite.RoomCatalog.Room.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class RoomChangesTest {

	private static Set<DedicatedRoom> mockSet;
	private static RoomChanges changes;
	private static PriceCalculator calculator;

	@BeforeAll
	public static void setUp(){

		CatalogRepository catalog = spy(CatalogRepository.class);
		mockSet = new HashSet<>();
		calculator= mock(PriceCalculator.class);
		changes = new RoomChanges(catalog);
		
		doAnswer(new Answer() {
			@Override
			public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
				List<DedicatedRoom> rooms = new ArrayList<>(mockSet);
				for (DedicatedRoom room : rooms){
					room.setPrice(calculator.calculateTotalPrice(room));
				}
				return null;
			}
		}).when(calculator).updatePrices();

		// mock behavior of catalog saving entry
		when(catalog.save(any(DedicatedRoom.class))).then(invocation -> {
			DedicatedRoom room = invocation.getArgument(0);
			mockSet.add(room);
			return room;
		});

		when(catalog.findAll()).then(invocation -> {
			return mockSet;
		});

		when(changes.getAllEquipment()).then(invocation -> {
			List<EquipmentItem> items = new ArrayList<EquipmentItem>();
			for (DedicatedRoom room : mockSet){
				items.addAll(room.getEquipment());
			}
			return items;
		});

		//Initialize some equipment
		EquipmentItem tv = new EquipmentItem("TV", 10.0);
		EquipmentItem doubleBed = new EquipmentItem("Queen-sized bed", 23.5);
		EquipmentItem carpet = new EquipmentItem("Carpet", 5.0);

		//To add the Equipment to the room, it has to be a set
		HashSet<EquipmentItem> equipmentItems = new HashSet<>();
		HashSet<EquipmentItem> carpetSet = new HashSet<>();
		HashSet<EquipmentItem> bedSet = new HashSet<>();

		//Add the Equipment to the empty Set
		equipmentItems.add(tv);
		carpetSet.add(carpet);
		bedSet.add(doubleBed);

		//Initialize some Rooms
		Room zimmer1 = new Room("Romantic Double",25.3, RoomType.DOUBLE, equipmentItems,"sdbr");
		Room zimmer2 = new Room("Spacious Single", 20.8, RoomType.SINGLE, equipmentItems,"asbr");
		Room suiteZimmer1 = new Room("Bedroom 1", 15.0, RoomType.DOUBLE, carpetSet);
		Room suiteZimmer2 = new Room("Master Bedroom", 30.0, RoomType.DOUBLE, bedSet);
		Room suiteZimmer3 = new Room("Living Room", 20.0, RoomType.SUITEROOM, carpetSet);

		//For a suite there are a Set of rooms required
		Set<Room> suiteRooms = new HashSet<>();
		suiteRooms.add(suiteZimmer1);
		suiteRooms.add(suiteZimmer2);
		suiteRooms.add(suiteZimmer3);

		//Initialize the suite
		Suite suite1 = new Suite("Grand Suite",suiteRooms, "");

		//save the rooms to the database/repository
		catalog.save(zimmer1);
		catalog.save(zimmer2);
		catalog.save(suite1);
		//calculator.updatePrices();
	}

	@Disabled
	@Test
	void printAllRoomsTest(){
		System.out.println(mockSet);
	}
}
