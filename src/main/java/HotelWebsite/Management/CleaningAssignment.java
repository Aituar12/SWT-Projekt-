package HotelWebsite.Management;

import HotelWebsite.RoomCatalog.Room.DedicatedRoom;
import HotelWebsite.user.RegisteredUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CleaningAssignment {

	@GeneratedValue
	@Id
	private Long id;

	@OneToOne
	private DedicatedRoom room;

	@ManyToMany
	private List<RegisteredUser> assignedStaff;

	public CleaningAssignment(DedicatedRoom room, List<RegisteredUser> assignedStaff) {
		this.room = room;
		this.assignedStaff = assignedStaff;
	}
}
