package HotelWebsite.Management;


import HotelWebsite.RoomCatalog.Room.DedicatedRoom;
import HotelWebsite.order.Reservation;
import HotelWebsite.order.ReservationRepository;
import HotelWebsite.user.RegisteredUser;
import HotelWebsite.user.RegisteredUserManagement;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CleaningAssignmentService {


	private final RegisteredUserManagement registeredUserManagement;
	private final ReservationRepository reservationRepository;

	private final CleaningAssignmentRepository cleaningAssignmentRepository;

	public CleaningAssignmentService(
			RegisteredUserManagement registeredUserManagement, 
			ReservationRepository reservationRepository, 
			CleaningAssignmentRepository cleaningAssignmentRepository){
		
		this.registeredUserManagement = registeredUserManagement;
		this.reservationRepository = reservationRepository;
		this.cleaningAssignmentRepository = cleaningAssignmentRepository;
	}

	public List<DedicatedRoom> getActiveRooms(ReservationRepository reservationRepository) {
		List<DedicatedRoom> activeBookedRooms = new ArrayList<>();
		List<Reservation> reservationList = reservationRepository.findAll().stream().toList();
		for (Reservation reservation: reservationList) {
			for (DedicatedRoom room: reservation.getRoomSet()) {
				activeBookedRooms.add(room);
			}
		}
		return activeBookedRooms;
	}

	public List<DedicatedRoom> getAssignedRooms(@LoggedIn RegisteredUser staff) {
		List<DedicatedRoom> assignedRooms = new ArrayList<>();

		for (CleaningAssignment cleaningAssignment: cleaningAssignmentRepository.findAll()) {
			if (cleaningAssignment.getAssignedStaff().contains(staff)) {
				assignedRooms.add(cleaningAssignment.getRoom());
			}
		}

		return assignedRooms;
	}
}
