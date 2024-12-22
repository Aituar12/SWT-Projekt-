package HotelWebsite.order;

import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Reservation.ProductIdentifier> {
	public List<Reservation> findAll();

	default void payReservation(Order.OrderIdentifier orderId) {
		for (Reservation reservation : findAll()) {
			if(reservation.getOrderId().equals(orderId)) {
				reservation.setPaymentStatus(PaymentStatus.PAID);
				save(reservation);
			}
		}
	}
}
