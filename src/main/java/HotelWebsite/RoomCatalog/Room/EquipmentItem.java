package HotelWebsite.RoomCatalog.Room;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class EquipmentItem implements Comparable<EquipmentItem> {
	@Id @GeneratedValue
	private Long id;
	private String name;
	//every equipment has a value (tv has more value than a radio)
	private double value;
	//later to filter all Rooms in the catalog, all rooms with this equipment are listed
	@ManyToMany(mappedBy = "equipment")
	private Set<DedicatedRoom> rooms;

	//Default Constructor
	public EquipmentItem() {}

	public EquipmentItem(String name, double value) {
		this.name = name;
		this.value = value;
	}

	//Getter
	public String getName() {
		return this.name;
	}

	public double getValue() {
		return this.value;
	}

	public Long getId() {
		return id;
	}

	public void setValue(double newAmount){
		this.value = newAmount;
	}

	public void setId(Long id) {
		this.id = id;
	}

	//To display the equipment items at the template, a toString method is needed
	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public int compareTo(EquipmentItem o) {
		return this.name.compareTo(o.name);
	}
}
