package HotelWebsite.RoomCatalog;

import HotelWebsite.RoomCatalog.Room.*;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import static org.salespointframework.core.Currencies.EURO;

@Service
public class CatalogFilter {

	private final CatalogRepository catalog;
	private static final Logger LOG = LoggerFactory.getLogger(PriceCalculator.class);


	@Autowired
	public CatalogFilter(CatalogRepository catalogRepository) {
		Assert.notNull(catalogRepository, "Repository shall not be null!");
		this.catalog = catalogRepository;
	}

	//Add rooms to the repository
	@Transactional
	public void addRoom(DedicatedRoom room) {
		catalog.save(room);
		LOG.info("Added room: " + room.getName());
	}

	public List<DedicatedRoom> filterByDedicated(boolean dedicated) {
		LOG.info("Existing rooms: "+catalog.findAll());
		LOG.info("Found separate rooms: " + catalog.findAllByDedicated(dedicated));
		return catalog.findAllByDedicated(dedicated).stream().toList();
	}

	public List<DedicatedRoom> filterByTime(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			return catalog.findAllByDedicated(false).stream().toList();
		}
		Set<DedicatedRoom> filteredRooms = new HashSet<>(catalog.findAllByDedicated(false));
		Set<LocalDate> dates = new HashSet<>();

		//calculate the difference between start and end date
		long diffDays = ChronoUnit.DAYS.between(startDate, endDate);
		//Iterate over all days
		for (int i = 0; i < diffDays; i++) {
			//add each day/night into the set (without the end date)
			dates.add(startDate.plusDays(i));
		}

		for (LocalDate date : dates) {
			for (DedicatedRoom room : catalog.findAllByDedicated(false)) {
				if (room.getBookedDates().contains(date)) {
					filteredRooms.remove(room);
				}
			}
		}
		return filteredRooms.stream().toList();
	}

	public List<EquipmentItem> getAllEquipment() {
		List<DedicatedRoom> rooms = filterByDedicated(false);
		Set<EquipmentItem> itemset = new HashSet<>();
		for (DedicatedRoom room : rooms) {
			itemset.addAll(room.getEquipment());
		}
		List<EquipmentItem> items = new ArrayList<>(itemset.stream().toList());
		Collections.sort(items);
		return items;
	}

	public List<EquipmentItem> itemsById(Long[] ids) {
		if (ids==null) {
			return null;
		}
		List<Long> identifiers = new ArrayList<>(List.of(ids));
		List<EquipmentItem> result = new ArrayList<>();
		for (EquipmentItem item : getAllEquipment()) {
			if (identifiers.contains(item.getId())){
				result.add(item);
			}
		}
		return result;
	}

	public List<DedicatedRoom> filterByEquipment(List<EquipmentItem> filter, List<DedicatedRoom> rooms) {
		if (filter == null) {
			return rooms;
		}

		List<DedicatedRoom> result = new ArrayList<>();
		for (DedicatedRoom room : rooms) {
			if (room.getEquipment().containsAll(filter)){
				result.add(room);
			}
		}
		return result;
	}

	public List<DedicatedRoom> filterByRoomType (RoomType[] filter, List<DedicatedRoom> rooms) {
		if (filter == null || filter.length == 0) {
			return rooms;
		}

		List<DedicatedRoom> result = new ArrayList<>();

		for (DedicatedRoom room: rooms) {
			for (RoomType type: filter) {
				if (room.getRoomType().equals(type)) {
					result.add(room);
				}
			}
		}
		return result;
	}

	public List<DedicatedRoom> filterByPrice(Double filter, List<DedicatedRoom> rooms) {
		if (filter == 0) {
			return rooms;
		}
		List<DedicatedRoom> result = new ArrayList<>();
		for (DedicatedRoom room:rooms) {
			if (room.getPrice().getNumber().longValue() < filter) {
				result.add(room);
			}
		}
		return result;
	}

	public List<DedicatedRoom> filterByBedCount(Integer filter, List<DedicatedRoom> rooms) {
		if (filter == 0 ) {
			return rooms;
		}

		List<DedicatedRoom> result = new ArrayList<>();
		for (DedicatedRoom room : rooms) {
			if (room.getBedCount() >= filter) {
				result.add(room);
			}
		}
		return result;
	}

	public List<DedicatedRoom> filterCatalog(LocalDate startDate, LocalDate endDate, Integer bedCount,
											 RoomType[] types, Double price, Long[] equipment ) {

		LOG.info("params: " + startDate + " to " + endDate +", " +  bedCount + ", " + types
			+ ", " + price + ", "+ equipment);
		List<DedicatedRoom> result = filterByTime(startDate, endDate);
		LOG.info("fitting rooms after time span:" + result);

		if (!result.isEmpty()) {
			result = filterByBedCount(bedCount, result);
			LOG.info("fitting rooms after bedcount: " + result);

			result = filterByRoomType(types, result);
			LOG.info("fitting rooms after roomtype: " + result);

			result = filterByPrice(price, result);
			LOG.info("fitting rooms after price: " + result);

			result = filterByEquipment(itemsById(equipment), result);
			LOG.info("fitting rooms after equipment: " + result);
		}

		return result;

	}

	public void calculateSetDatesPrice(LocalDate startDate, LocalDate endDate){
		long diffDay;
		//If start and end date is not set they are null therefore diffDay would be null
		if(startDate == null || endDate == null) {
			//To catch this error diffDay is set to 1 if there are no start and endDate set
			diffDay = 1L;
		}
		else {
			diffDay = ChronoUnit.DAYS.between(startDate, endDate);
		}
		List<DedicatedRoom> catalogList2 = filterByTime(startDate, endDate);
		for (DedicatedRoom room : catalogList2) {
			MonetaryAmount totalPrice = room.getPrice().multiply(diffDay);
			room.setTotalPrice(totalPrice);
		}

	}

}
