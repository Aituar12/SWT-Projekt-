package HotelWebsite.order;

import HotelWebsite.RoomCatalog.Room.DedicatedRoom;
import HotelWebsite.RoomCatalog.Room.Room;
import HotelWebsite.RoomCatalog.Room.Suite;
import jakarta.persistence.*;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Order;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.salespointframework.core.Currencies.EURO;


@Entity
public class Reservation extends Product {
	
	private @Id @GeneratedValue long id;

	private Long diffDays;

	private Long reservationNumber;
	private TimeStatus timeStatus;

	private PaymentStatus paymentStatus;

	private Order.OrderIdentifier orderId;

	// roomSet contains all booked dedicated rooms
	@ManyToMany
	private Set<DedicatedRoom> roomSet = new HashSet<>();

	// start date of the reservation
	private LocalDate startDate;

	// end date of the reservation
	private LocalDate endDate;

	private Board boardType;

	private Integer personCount;

	public enum Board {
		BREAKFAST,
		HALFBOARD,
		FULLBOARD
}

	public Reservation() {}

	public Reservation(String name, MonetaryAmount price, Set<DedicatedRoom> roomSet) {
		super(name, price);
		this.roomSet = roomSet;
	}

	public Set<DedicatedRoom> getRoomSet(){
		return roomSet;
	}

	public void addRoom(DedicatedRoom room) {
		roomSet.add(room);
	}

	// Updating Reservation price after adding dates and rooms to Reservation
	public void updatePrice() {
		Double totalPrice = 0.0;
		if(this.startDate == null || this.endDate == null) {
			System.out.println("Das Datum wurde nicht gesetzt.");
		} else {
			// adding up every single price per night of each booked room in the for-loop
			for (DedicatedRoom room : roomSet) {
				totalPrice += room.getPrice().getNumber().doubleValue();
			}
			// calculating total price of the reservation by multiplying with the amount of booked nights
			Long diffDays = ChronoUnit.DAYS.between(startDate,endDate);
			totalPrice *= diffDays;
			super.setPrice(Money.of(totalPrice,EURO));
		}
	}

	// Getter functions

	public LocalDate getStartDate() {
		return this.startDate;
	}

	public LocalDate getEndDate() {
		return this.endDate;
	}

	public Board getBoardType() {
		return boardType;
	}

	public Integer getPersonCount() {
		return personCount;
	}

	public Order.OrderIdentifier getOrderId() { return this.orderId; }

	// Calculates TimeStatus before returning, remind: status is not saved in the repository by itself
	public TimeStatus getTimeStatus() {
		if(LocalDate.now().isAfter(this.endDate)) {
			this.timeStatus = TimeStatus.EXPIRED;
		} else if (LocalDate.now().isBefore(this.startDate)) {
			this.timeStatus = TimeStatus.PENDING;
		} else {
			this.timeStatus = TimeStatus.AKTIV;
		}
		return this.timeStatus;
	}

	public Long getDiffDays() { return ChronoUnit.DAYS.between(this.startDate,this.endDate); }

	public Long getReservationNumber() { return this.reservationNumber; }

	public Reservation getReservation() { return this;}

	public PaymentStatus getPaymentStatus() { return this.paymentStatus; }

	// Setter functions

	public void setReservationNumber(Long reservationNumber) { this.reservationNumber = reservationNumber; }

	public void setOrderID(Order.OrderIdentifier orderID) {
		this.orderId = orderID;
	}

	public void  setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus;}

	public void setBoardType(Board boardType) {
		this.boardType = boardType;
	}

	public void setPersonCount(Integer personCount) {
		this.personCount = personCount;
	}

}
