package HotelWebsite.RoomCatalog;

import HotelWebsite.RoomCatalog.Room.*;
import org.salespointframework.core.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CatalogDataInitializer implements DataInitializer {


	@Autowired
	private CatalogFilter catalog;
  
  	@Autowired
  	private PriceCalculator priceCalculator;

	//Default Constructor
	public CatalogDataInitializer() {
		//Default Constructor
  	}

	@Override
	public void initialize() {
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

		//Initialize some Rooms
		Room zimmer1 = new Room("Romantic Double",25.3, RoomType.DOUBLE, equipmentItems,"sdbr");
		Room zimmer2 = new Room("Spacious Single", 20.8, RoomType.SINGLE, equipmentItems,"asbr");
		Room suiteZimmer1 = new Room("Bedroom 1", 15.0, RoomType.DOUBLE, carpetSet);
		Room suiteZimmer2 = new Room("Master Bedroom", 30.0, RoomType.DOUBLE, bedSet);
		Room suiteZimmer3 = new Room("Living Room", 20.0, RoomType.SUITEROOM, carpetSet);
		Room suiteZimmer4 = new Room("Kitchen", 15.0, RoomType.SUITE, kitchenSet);

		//For a suite there are a Set of rooms required
		Set<Room> suiteRooms = new HashSet<>();
		suiteRooms.add(suiteZimmer1);
		suiteRooms.add(suiteZimmer2);
		suiteRooms.add(suiteZimmer3);

		//Initialize the suite
		Suite suite1 = new Suite("Grand Suite",suiteRooms, "gsp");

		//save the rooms to the database/repository

		catalog.addRoom(zimmer1);
		catalog.addRoom(zimmer2);
		catalog.addRoom(suiteZimmer4);
		catalog.addRoom(suite1);
    
		//Calculate the room prices -> needs to be called after rooms are added to the repo
		priceCalculator.updatePrices();
	}
}
