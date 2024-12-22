package HotelWebsite.Management;

import HotelWebsite.RoomCatalog.CatalogRepository;
import HotelWebsite.RoomCatalog.Room.DedicatedRoom;
import HotelWebsite.RoomCatalog.Room.EquipmentItem;
import HotelWebsite.RoomCatalog.Room.PriceCalculator;
import HotelWebsite.order.Reservation;
import HotelWebsite.order.ReservationRepository;
import HotelWebsite.user.RegisteredUser;
import HotelWebsite.user.RegisteredUserManagement;
import org.salespointframework.catalog.Product.ProductIdentifier;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ManagementController {

	private final Statistic statistic;
	private final PriceCalculator calculator;
	private final CleaningAssignmentService cleaningAssignmentService;
	private final RegisteredUserManagement registeredUserManagement;
	private final RoomChanges roomChanges;
	private final CatalogRepository catalog;
	private static final Logger LOG = LoggerFactory.getLogger(ManagementController.class);
	private final ReservationRepository reservationRepository;
	private final CleaningAssignmentRepository cleaningAssignmentRepository;

	public ManagementController(Statistic statistic,
								PriceCalculator calculator,
								CleaningAssignmentService cleaningAssignmentService,
								RegisteredUserManagement registeredUserManagement,
								ReservationRepository reservationRepository, CleaningAssignmentRepository cleaningAssignmentRepository,
								RoomChanges roomChanges,
								CatalogRepository catalog) {
		this.statistic = statistic;
		this.calculator = calculator;
		this.cleaningAssignmentService = cleaningAssignmentService;
		this.registeredUserManagement = registeredUserManagement;
		this.reservationRepository = reservationRepository;
		this.cleaningAssignmentRepository = cleaningAssignmentRepository;
		this.roomChanges = roomChanges;
		this.catalog = catalog;
	}


	@GetMapping("/management")
	public String management() {
		return "management";
	}

	@GetMapping("/management/priceChanges")
	public String priceChanges(Model model) {
		model.addAttribute("calculator", calculator);
		model.addAttribute("QMPrice", calculator.getPricePerQM());
		model.addAttribute("BedPrice", calculator.getPricePerBed());
		model.addAttribute("equipmentItems", roomChanges.getAllEquipment());
		model.addAttribute("catalog", catalog.findAllByDedicated(false));
		model.addAttribute("roomChanges", roomChanges);
		return "priceChanges";
	}

	@PostMapping("/management/updateQMPrice")
	public String updateQMPrice(@RequestParam("QMPrice") double newPrice) {
		calculator.setPricePerQM(newPrice);
		return "redirect:/management/priceChanges";
	}

	@PostMapping("/management/updateBedPrice")
	public String updateBedPrice(@RequestParam("BedPrice") double newPrice) {
		calculator.setPricePerBed(newPrice);
		return "redirect:/management/priceChanges";
	}

	@PostMapping("/management/updateItem")
	public String updateItemPrice(@RequestParam("id") Long id,
								  @RequestParam("newPrice") double newPrice) {
		roomChanges.updateItem(id, newPrice);
		calculator.updatePrices();
		return "redirect:/management/priceChanges";
	}

	@PostMapping("/management/addInventory")
	public String addInventory(@RequestParam("name") String name,
							   @RequestParam("price") double price,
							   @RequestParam(name = "rooms", required = false) ProductIdentifier[] rooms,
							   RedirectAttributes redirectAttributes
	) {
		if (rooms == null ) {
			redirectAttributes.addFlashAttribute("errorRooms", "ERROR : Must add new Equipment to room");
			return "redirect:/management/priceChanges";
		}
		for (EquipmentItem item : roomChanges.getAllEquipment()) {
			if (item.getName().equalsIgnoreCase(name)) {
				redirectAttributes.addFlashAttribute("errorItem", "ERROR : Equipment with same name already present");
				return "redirect:/management/priceChanges";
			}
		}
		roomChanges.addItem(name, price, rooms);
		calculator.updatePrices();
		redirectAttributes.addFlashAttribute("success", "SUCCESS : Equipment sucessfully added");
		return "redirect:/management/priceChanges";
	}

	@PostMapping("/management/addItem")
	public String addItem(@RequestParam("room") ProductIdentifier id,
						  @RequestParam(name = "items", required = false) Long[] items,
						  RedirectAttributes redirectAttributes){
		if (items == null ) {
			redirectAttributes.addFlashAttribute("errorAdding", "ERROR : Must add new Equipment to room");
			return "redirect:/management/priceChanges";
		}
		roomChanges.addItemToRoom(id, items);
		calculator.updatePrices();
		return "redirect:/management/priceChanges";
	}

	@PostMapping("/management/removeItem")
	public String removeItem(@RequestParam("room") ProductIdentifier id,
							 @RequestParam(name = "items", required = false) Long[] items,
							 RedirectAttributes redirectAttributes){
		if (items == null ) {
			redirectAttributes.addFlashAttribute("errorRemoving", "ERROR : Must remove Equipment from room");
			return "redirect:/management/priceChanges";
		}
		roomChanges.removeItemFromRoom(id, items);
		calculator.updatePrices();
		return "redirect:/management/priceChanges";
	}

	@GetMapping("management/statistics")
	public String statistics(Model model) {

		model.addAttribute("revenueYesterday", statistic.getRevenue(1));
		model.addAttribute("totalRevenueYesterday", statistic.getRevenue(1).stream()
			.mapToDouble(TransactionEntry::getAmount)
			.sum());

		model.addAttribute("revenueLastWeek", statistic.getRevenue(7));
		model.addAttribute("totalRevenueLastWeek", statistic.getRevenue(7).stream()
			.mapToDouble(TransactionEntry::getAmount)
			.sum());

		model.addAttribute("revenueLastMonth", statistic.getRevenue(30));
		model.addAttribute("totalRevenueLastMonth", statistic.getRevenue(30).stream()
			.mapToDouble(TransactionEntry::getAmount)
			.sum());

		model.addAttribute("expensesYesterday", statistic.getExpenses(1));
		model.addAttribute("totalExpensesYesterday", statistic.getExpenses(1).stream()
			.mapToDouble(TransactionEntry::getAmount)
			.sum());

		model.addAttribute("expensesLastWeek", statistic.getExpenses(7));
		model.addAttribute("totalExpensesLastWeek", statistic.getExpenses(7).stream()
			.mapToDouble(TransactionEntry::getAmount)
			.sum());

		model.addAttribute("expensesLastMonth", statistic.getExpenses(30));
		model.addAttribute("totalExpensesLastMonth", statistic.getExpenses(30).stream()
			.mapToDouble(TransactionEntry::getAmount)
			.sum());

		return "statistics";
	}

	@GetMapping("management/cleaning")
	public String cleaning(Model model) {
		//adding the designated cleaning staff to be displayed for assignment
		model.addAttribute("cleaningStaff", registeredUserManagement.findAllByDepartment("Cleaning"));

		List<DedicatedRoom> activeBookedRooms = new ArrayList<>();
		List<DedicatedRoom> lastDayBookedRooms = new ArrayList<>();

		//filters out active reservations (currently occupied rooms)
		for (Reservation reservation: reservationRepository.findAll().stream().toList()) {
			String tempTimeStatus = String.valueOf(reservation.getTimeStatus());
			if(tempTimeStatus.equals("AKTIV")) {
				activeBookedRooms.addAll(reservation.getRoomSet());
			}
		}

		//filters out final cleanings out of active reservations
		for (Reservation reservation: reservationRepository.findAll().stream().toList()) {
			if (reservation.getEndDate().equals(LocalDate.now())) {
				lastDayBookedRooms.addAll(reservation.getRoomSet());
				activeBookedRooms.removeAll(reservation.getRoomSet());
			}
		}

		model.addAttribute("activeBookedRooms",activeBookedRooms);
		model.addAttribute("lastDayBookedRooms", lastDayBookedRooms);

		return "cleaning";
	}

	@PostMapping("/management/cleaning/addStaffToRoom")
	public String addStaffToRoom(
		@RequestParam(value = "selectedStaffMembers") List<String> selectedStaffMembers,
		@RequestParam(value= "roomId") String roomId) {

		//getting Room for roomId
		DedicatedRoom roomToAssign = null;
		//initializing List of assigned staff for the CleaningAssignment
		List<RegisteredUser> staffToAssign = new ArrayList<>();

		for (DedicatedRoom room: catalog.findAll()) {
			String tempRoomId = String.valueOf(room.getId());
			if (tempRoomId.equals(roomId)) {
				roomToAssign = room;
			}
		}

		for (String tempSelectedStaffMember : selectedStaffMembers) {
			for (RegisteredUser staff: registeredUserManagement.findall()) {
				String tempStaffId = staff.getUserAccount().getId().toString();
				if (tempSelectedStaffMember.equals(tempStaffId)) {
					staffToAssign.add(staff);
				}
			}
		}

		//creating Assignment and saving it to database
		CleaningAssignment cleaningAssignmentDedicatedRoom = new CleaningAssignment(roomToAssign, staffToAssign);

		//updating cleaningassignments: if assignment for room already exits, delete old one to save new one
		for (CleaningAssignment assignment : cleaningAssignmentRepository.findAll()) {
			if (assignment.getRoom().equals(roomToAssign)) {
				cleaningAssignmentRepository.delete(assignment);
			}
		}
		cleaningAssignmentRepository.save(cleaningAssignmentDedicatedRoom);
		System.out.println(cleaningAssignmentDedicatedRoom);

		return "redirect:/management/cleaning";
		}

	@GetMapping("myCleaningSchedule")
	public String getRoomAssignmentsPerStaff(Model model,
											 @LoggedIn UserAccount userAccount) {

		RegisteredUser registeredUser= null;
		for (RegisteredUser user : registeredUserManagement.findAllByDepartment("Cleaning")) {
			if (user.getUserAccount().equals(userAccount)){
				registeredUser = user;
			}
		}

		model.addAttribute("assignedRooms",cleaningAssignmentService.getAssignedRooms(registeredUser));

		return "myCleaningSchedule";
	}

	@GetMapping("/reservationOverview")
	public String reservationOVerview(Model model) {

		model.addAttribute("allReservations", reservationRepository.findAll().stream().toList());

		return "reservationOverview";
	}

}

