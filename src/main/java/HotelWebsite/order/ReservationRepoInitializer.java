package HotelWebsite.order;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationRepoInitializer implements DataInitializer {
	private final UniqueInventory<UniqueInventoryItem> reservationInventory;

	@Autowired
	private final ReservationRepository reservationRepository;

	ReservationRepoInitializer(UniqueInventory<UniqueInventoryItem> reservationInventory,
							   ReservationRepository reservationRepository) {
		this.reservationInventory = reservationInventory;
		this.reservationRepository = reservationRepository;
	}

	@Override
	public void initialize() {

		// Iterating over all rooms in the Catalog. If no inventory item is linked, a new one is created.
		reservationRepository.findAll().forEach(
			reservation -> {
				if (reservationInventory.findByProductIdentifier(reservation.getId()).isEmpty()) {
					reservationInventory.save(new UniqueInventoryItem(reservation, Quantity.of(1)));
				}
			}
		);
	}
}
