package HotelWebsite.RoomCatalog.Room;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import javax.money.MonetaryAmount;
import static org.salespointframework.core.Currencies.EURO;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity
public abstract class DedicatedRoom extends Product {
	/*
	* This Class has the following tasks:
	*
	* -> Easier display of Rooms and Suites in the Catalog (it represents the products of the catalog)
	*
	* -> Handle Rooms which are part of a Suite and don't display them in the Catalog
	*/
	private String image;

	//this attribute shows if a room is part of a Suite or not
	private boolean dedicated = false;

	private boolean inCart = false;

	// One room can have many equipment items
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "RoomEquipment",
		joinColumns = @JoinColumn(name = "RoomId"),
		inverseJoinColumns = @JoinColumn(name = "EquipmentId")
	)
	private Set<EquipmentItem> equipment;
	private int bedCount;
	private double area;
	@GeneratedValue
	private int roomNumber;
	private RoomType roomType;
	@Enumerated(EnumType.STRING)
	private Board boardType;
	private int personCount;

	private MonetaryAmount totalPrice;

	//Save the booked nights (14.01 to 15.01 -> 14.01 saved)
	private Set<LocalDate> bookedDates = new HashSet<>();
	
	public enum Board {
			BREAKFAST,
			HALFBOARD,
			FULLBOARD
	}

	//Constructor for Rooms without an image
	public DedicatedRoom(String name, RoomType roomType, double area, Set<EquipmentItem> equipment) {
		super(name, Money.of(0.0, EURO));
		this.area = area;
		this.equipment = equipment;
		this.roomType = roomType;
	}

	//Constructor for Rooms with an image
	public DedicatedRoom(String name, RoomType roomType, double area, Set<EquipmentItem> equipment, String image) {
		super(name, Money.of(0.0, EURO));
		this.area = area;
		this.equipment = equipment;
		this.image = image;
		this.roomType = roomType;
	}

	//Constructor especially for a Suite
	public DedicatedRoom(String name, RoomType roomType) {
		super(name, Money.of(0.0, EURO));
		this.roomType = roomType;
		this.area = 0.0;
		this.equipment = new HashSet<>();
		this.roomType = RoomType.SUITE;
	}

	//Add all nights into the bookedDates set
	public void setBookedDates(LocalDate startDate, LocalDate endDate) {
		//calculate the difference between start and end date
		Long diffDays = ChronoUnit.DAYS.between(startDate,endDate);
		for (int i=0; i < diffDays; i++) {
			//add each day/night into the set (without the end date)
			this.bookedDates.add(startDate.plusDays(i));
		}
	}

	// Remove booked nights from the bookedDates set (used when order is cancelled)
	public void removeBookedDates(LocalDate startDate, LocalDate endDate) {
		Long diffDays = ChronoUnit.DAYS.between(startDate,endDate);
		for (int i=0; i < diffDays; i++) {
			//remove each day/night from the set (without the end date)
			this.bookedDates.remove(startDate.plusDays(i));
		}
	}


	// Getter
	public int getBedCount() {
		return bedCount;
	}

	public Set<EquipmentItem> getEquipment() {
		return equipment;
	}

	public String getImage() {
		return image;
	}

	public boolean getDedicated() {
		return dedicated;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public double getArea() {
		return area;
	}

	public RoomType getRoomType() {
		return roomType;
	}

	public boolean getInCart() {
		return inCart;
	}

	public Set<LocalDate> getBookedDates() {
		return this.bookedDates;
	}

	public Board getBoardType() {
		return this.boardType;
	}

	public int getPersonCount() {
		return personCount;
	}

	public MonetaryAmount getTotalPrice() {return totalPrice;}

	//Setter
	public void setArea(double area) {
		this.area = area;
	}

	public void setBedCount(int bedCount) {
		this.bedCount = bedCount;
	}

	public void setDedicated(boolean dedicated) {
		this.dedicated = dedicated;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setEquipment(Set<EquipmentItem> equipment) {
		this.equipment = equipment;
	}

	public void setInCart(Boolean state) {this.inCart = state;}

	public void addEquipment(EquipmentItem item) {
		equipment.add(item);
	}

	public void removeEquipment(EquipmentItem item) {
		equipment.remove(item);
	}

	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}

	public void setBoardType(Board boardType) {
		this.boardType = boardType;
	}

	public void setPersonCount(int personCount) {
		this.personCount = personCount;
	}

	public void setTotalPrice(MonetaryAmount totalPrice){this.totalPrice=totalPrice;}

	@Override
	public String toString(){
		return this.getName() + " " + this.getBedCount() + " beds";
	}
}
