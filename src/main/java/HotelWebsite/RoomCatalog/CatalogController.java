package HotelWebsite.RoomCatalog;

import HotelWebsite.RoomCatalog.Room.DedicatedRoom;
import HotelWebsite.RoomCatalog.Room.EquipmentItem;
import HotelWebsite.RoomCatalog.Room.PriceCalculator;
import HotelWebsite.RoomCatalog.Room.RoomType;
import HotelWebsite.RoomCatalog.Room.DedicatedRoom.Board;
import HotelWebsite.order.DateHolder;

import org.salespointframework.order.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class CatalogController {

	@Autowired
	private CatalogFilter catalog;
	int bedCount;
	RoomType[] types;
	double price;
	Long[] equipment;
	private final DateHolder dateHolder;
	Board boardType;
	int personCount;

	private static final Logger LOG = LoggerFactory.getLogger(PriceCalculator.class);


	public CatalogController(CatalogFilter catalog, DateHolder dateHolder) {
		this.catalog = catalog;
		this.dateHolder = dateHolder;
		bedCount = 0;
		types = null;
		price = 0;
		equipment = null;


	}
	@GetMapping("/")
	public String index(Model model) {
		// get all the room type possibilities but exclude suiteroom
		RoomType[] allTypes = Arrays.stream(RoomType.values())
			.filter(type -> type != RoomType.SUITEROOM)
			.toArray(RoomType[]::new);
		model.addAttribute("allTypes", allTypes);
		return "welcome";
	}

	/**
	 * Catalog string. To show all available rooms, without filtering
	 *
	 * @param model the model
	 * @return the string
	 */
	@GetMapping("/catalog")
	public String catalog(Model model) {

		// get all the room type possibilities but exclude suiteroom for the filter sidebar
		RoomType[] allTypes = Arrays.stream(RoomType.values())
			.filter(type -> type != RoomType.SUITEROOM)
			.toArray(RoomType[]::new);
		model.addAttribute("allTypes", allTypes);

		// get all the possible room equipments for the sidebar
		model.addAttribute("equipment", catalog.getAllEquipment());

		List<DedicatedRoom> catalogList  = catalog.filterCatalog(dateHolder.getStartDate(), dateHolder.getEndDate(),
			this.bedCount, this.types, this.price, this.equipment);

		// make attribute for the types pretty
		List<String> currentTypes = new ArrayList<>();
		if (types!=null) {
			for (RoomType type : this.types) {
				currentTypes.add(type.name());
			}
		}

		// make attribute for the equipment pretty
		List<String> currentEquipment = new ArrayList<>();
		if(equipment!=null) {
			for (EquipmentItem item : catalog.itemsById(this.equipment)) {
				currentEquipment.add(item.getName());
			}
		}

		//Calculate the total price of the rooms
		catalog.calculateSetDatesPrice(dateHolder.getStartDate(), dateHolder.getEndDate());

		// add currently selected values to the model
		model.addAttribute("currentBeds", this.bedCount);
		model.addAttribute("currentTypes", currentTypes);
		model.addAttribute("currentPrice", this.price);
		model.addAttribute("currentEquipment", currentEquipment);
		model.addAttribute("currentPersonCount", this.personCount);
		model.addAttribute("currentBoardType", this.boardType);
		model.addAttribute("startDate", dateHolder.getStartDate());
		model.addAttribute("endDate",dateHolder.getEndDate());
		// adding other attributes
		model.addAttribute("errorMessage",model.getAttribute("errorMessage"));
		model.addAttribute("catalog", catalogList);
		model.addAttribute("title", "catalog.room" );
		return "catalog";
	}

	@GetMapping("/room/{room}")
	public String room(@PathVariable DedicatedRoom room, Model model) {
		model.addAttribute("title", room.getName());
		model.addAttribute("room", room);
		return "room";
	}

	/**
	 * Filter catalog and add the fitting rooms to the model.
	 * All parameters are optional so that only newly set parameters are used for the filtering.
	 * This combines the former method setDates with the rest of the filter logic
	 * These are needed for the date setting part
	 * @param model              the model
	 * @param cart               the cart
	 * @param redirectAttributes the redirect attributes
	 * @param startDate          the start date
	 * @param endDate            the end date
	 * The rest of the filter logic
	 * @param bedCount           the bed count
	 * @param types              the types
	 * @param price              the price
	 * @param equipment          the equipment
	 * All the parameters are optional, so that it only updates newly made choices.
	 * Choices that have not been set in a request are ignored, and the old values of previous requests are used
	 * @return the catalog as string for html
	 */
	@PostMapping("/filterCatalog")
	public String filterCatalog(Model model,
								@ModelAttribute Cart cart,
								RedirectAttributes redirectAttributes,
								@RequestParam(name = "startDate", required = false) String startDate,
								@RequestParam(name = "endDate", required = false) String endDate,
								@RequestParam(name = "personCount", required = false) Integer personCount,
								@RequestParam(name = "boardType", required = false) String boardType,
								@RequestParam(name = "bedCount", required = false) Integer bedCount,
								@RequestParam(name = "types", required = false) RoomType[] types,
								@RequestParam(name = "price", required = false) Double price,
								@RequestParam(name = "equipment", required = false) Long[] equipment){


		String redirect = "redirect:/catalog";
		// setting dates logic and error handling
		String errorMessage = validateAndSetDates(startDate, endDate, cart);
		if (errorMessage != null) {
			redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
			return redirect;
		}
		// update the filter values, but only if new ones have been set, otherwise the old ones stay!
		if (bedCount != null) {
			this.bedCount = bedCount;
		}
		if (price != null) {
			this.price = price;
		}
		if (equipment != null) {
			this.equipment = equipment;
		}
		if (types != null) {
			this.types = types;
		}
		if (boardType != null) {
			this.boardType = Board.valueOf(boardType);
		}
		if (personCount != null) {
			this.personCount = personCount;
		}


		return redirect;
	}


	private String validateAndSetDates(String startDate, String endDate, @ModelAttribute Cart cart) {
		String errorMessage=null;
		if (!startDate.isEmpty() && !endDate.isEmpty()) {
			if (!cart.isEmpty()) {
				errorMessage= "Your date is already set. Remove all items from the cart to reset the dates";
			} else if (startDate.isEmpty() || endDate.isEmpty()) {
				errorMessage = "Set your desired start- and end dates.";
			} else {
				LocalDate setStartDate = LocalDate.parse(startDate);
				LocalDate setEndDate = LocalDate.parse(endDate);
				if (setStartDate.isBefore(LocalDate.now())) {
					errorMessage = "Start date has not to be in the past.";
				} else if (setStartDate.isAfter(setEndDate) || setStartDate.isEqual(setEndDate)) {
					errorMessage = "End date has to be set at least one day after the start date.";
				} else {
					dateHolder.setStartDate(LocalDate.parse(startDate));
					dateHolder.setEndDate(LocalDate.parse(endDate));
				}
			}
		}
		return errorMessage; // No error message
	}

	/**
	 * Reset all filters exept dates
	 *
	 * @return a redirect to the catalog
	 */
	@PostMapping("/resetFilters")
	public String resetFilters(){
		this.bedCount = 0;
		this.price=0;
		this.equipment = null;
		this.types = null;
		this.boardType = null;
		this.personCount = 0;

		return "redirect:/catalog";
	}

}
