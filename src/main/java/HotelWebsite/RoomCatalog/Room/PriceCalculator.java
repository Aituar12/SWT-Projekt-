package HotelWebsite.RoomCatalog.Room;

import HotelWebsite.RoomCatalog.CatalogRepository;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.salespointframework.core.Currencies.EURO;
import javax.money.MonetaryAmount;
import java.util.List;
import java.util.Set;

@Component
@Service
public class PriceCalculator {
	private double pricePerQM;
	private double pricePerBed;

	private CatalogRepository repo;

	private static final Logger LOG = LoggerFactory.getLogger(PriceCalculator.class);
	public PriceCalculator(CatalogRepository repo) {
		Assert.notNull(repo, "Repository shall not be null!");
		this.pricePerBed = 10;
		this.pricePerQM = 1.5;
		this.repo = repo;
	}

	// To calculate the total price of a single room, we first have to calculate the price of the extra equipment
	public double calculateEquipment(Set<EquipmentItem> items) {
		double equipmentPrice = 0.0;

		//Iterate over the entries of the equipment list to summarize the equipment price
		for(EquipmentItem equipment:items) {
			equipmentPrice = equipmentPrice + equipment.getValue();
		}
		return equipmentPrice;
	}

	// After that we calculate the price of the room itself (area and amount of beds)
	public double calculateRoomPrice(DedicatedRoom room) {
		double roomPrice;
		roomPrice = (room.getArea() * pricePerQM) + (room.getBedCount() * pricePerBed);
		return roomPrice;
	}

	// In the end we calculate the total price of the entire room including equipment
	public MonetaryAmount calculateTotalPrice(DedicatedRoom room) {
		return Money.of(calculateEquipment(room.getEquipment()) + calculateRoomPrice(room),EURO);
	}

	//Setter and getter
	@Transactional
	public void setPricePerQM(double pricePerQM) {
		this.pricePerQM = pricePerQM;
		updatePrices();
	}

	@Transactional
	public void setPricePerBed(double pricePerBed) {
		this.pricePerBed = pricePerBed;
		updatePrices();
	}

	public double getPricePerQM() {
		return  this.pricePerQM;
	}

	public double getPricePerBed() {
		return this.pricePerBed;
	}


	/**
	 * Update prices. since PriceCalculator is globally valid,
	 * whenever this is called, all room prices are updated according to the current
	 * price per square meter, and price per bed
	 * THIS NEEDS TO BE CALLED WHENEVER WE WANT TO CHANGE THE PRICES OF SOMETHING;
	 * OR IF NEW ROOMS ARE ADDED
	 * SO THAT IT IS UPDATED IN THE CATALOG DYNAMICALLY
	 */

	@Transactional
	public void updatePrices(){
		List<DedicatedRoom> rooms = repo.findAll().stream().toList();
		for (DedicatedRoom room : rooms){
			room.setPrice(calculateTotalPrice(room));
		}
		LOG.info("updated Room Prices");
	}

}
