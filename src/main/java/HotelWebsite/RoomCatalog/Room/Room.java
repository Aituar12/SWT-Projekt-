package HotelWebsite.RoomCatalog.Room;

import HotelWebsite.order.*;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Room extends DedicatedRoom {
	// private Set<LocalDate> bookedDates = new HashSet<>();

	@ManyToMany
	private Set<Reservation> linkedReservations = new HashSet<>();

	//Default Constructor
	public Room() {}

    public Room(String name, double area, RoomType roomType, Set<EquipmentItem> equipment){
		super(name, roomType, area, equipment);
		determineBedCount();
    }

	public Room(String name, double area, RoomType roomType, Set<EquipmentItem> equipment, String image){
		super(name, roomType, area, equipment, image);
		determineBedCount();
	}

	//Determine bed number of given
	private void determineBedCount() {
		switch (this.getRoomType()) {
			//If roomType is set to Suite -> Change to SuiteRoom (Suite is not allowed as a Room class)
			case SUITE -> {
				setRoomType(RoomType.SUITEROOM);
				setBedCount(0);
				//Because of the fact, that this room is part of a Suite -> set dedicated = true
				setDedicated(true);
			}
			//Represents a room in a suite with no beds (like a living room)
			case SUITEROOM -> {
				setBedCount(0);
				//same here -> set dedicated = true
				setDedicated(true);
			}
			case DOUBLE -> setBedCount(2);

			case SINGLE -> setBedCount(1);
		}
	}
}
