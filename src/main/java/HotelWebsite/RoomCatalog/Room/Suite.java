package HotelWebsite.RoomCatalog.Room;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Suite extends DedicatedRoom {

	//One Suite consists of multiple rooms
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "roomId")
	private Set<Room> rooms;

	//Constructors
	public Suite() {}

	public Suite(String name, Set<Room> rooms) {
		super(name, RoomType.SUITE);
		this.rooms = rooms;
		this.setArea(this.calculateTotalArea());
		this.setBedCount(this.calculateTotalBedCount());
		this.setEquipment(this.totalEquipment());
		//Now we change the room Status of all Suite rooms
		changeRoomStatus();
	}

	public Suite(String name, Set<Room> rooms, String image) {
		super(name, RoomType.SUITE);
		this.rooms = rooms;
		this.setImage(image);
		this.setArea(this.calculateTotalArea());
		this.setBedCount(this.calculateTotalBedCount());
		this.setEquipment(this.totalEquipment());
		//Now we change the room Status of all Suite rooms
		changeRoomStatus();
	}

	//Summarize the number of beds in a suite
	public int calculateTotalBedCount() {
		int bedCount = 0;
		for(Room room:rooms) {
			bedCount = bedCount + room.getBedCount();
		}
		return bedCount;
	}

	//Summarize the total area of the Suite rooms
	public double calculateTotalArea() {
		double area = 0.0;
		for (Room room:rooms) {
			area += room.getArea();
		}
		return area;
	}

	//Get all equipment of the suite in a Set
	public Set<EquipmentItem> totalEquipment() {
		Set<EquipmentItem> suiteEquipment = new HashSet<>();
		for (Room room:rooms) {
			suiteEquipment.addAll(room.getEquipment());
		}
		return suiteEquipment;
	}

	//Change the Status of the rooms (dedicated) to not show the rooms of the Suite in the Catalog
	public void changeRoomStatus() {
		for (Room room:rooms) {
			room.setDedicated(true);
		}
	}

	//Functions to add and remove rooms from a suite
	public void addRoom(Room room) {
		this.rooms.add(room);
	}

	public void removeRoom(Room room) {
		this.rooms.remove(room);
	}

	//getter

	public Set<Room> getRooms() {
		return rooms;
	}

	//Setter

	public void setRooms(Set<Room> rooms) {
		this.rooms = rooms;
	}
}
