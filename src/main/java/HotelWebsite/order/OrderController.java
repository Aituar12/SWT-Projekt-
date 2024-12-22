/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package HotelWebsite.order;

import HotelWebsite.RoomCatalog.*;
import HotelWebsite.order.PaymentStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collector;

import HotelWebsite.RoomCatalog.Room.*;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.salespointframework.core.Currencies.EURO;


@Controller
@SessionAttributes({"cart","startDate","endDate"})
public class OrderController {

    // RoomCatalog dependency
	@Autowired
    private final CatalogRepository roomCatalog;

    // OrderManagement dependency for reservations
    private final OrderManagement<Order> orderManagement;

	@Autowired
	private final ReservationRepository reservationRepository;

	@Autowired
	private final UniqueInventory<UniqueInventoryItem> reservationInventory;

	@Autowired
	private DateHolder dateHolder;

	@Autowired
	private BoardAndPersonHolder boardAndPersonHolder;

    // Constructor to initialize OrderController with OrderManagement and RoomCatalog
    OrderController(OrderManagement<Order> orderManagement,
					CatalogRepository roomCatalog,
					UniqueInventory<UniqueInventoryItem> reservationInventory,
					ReservationRepository reservationRepository) {
		// Assertion check for ensuring non-null OrderManagement
        Assert.notNull(orderManagement, "OrderManagement must not be null!");
        this.orderManagement = orderManagement;
        this.roomCatalog = roomCatalog;
		this.reservationInventory = reservationInventory;
		this.reservationRepository = reservationRepository;

    }

    // Initializing the 'cart' attribute as a Cart object
    @ModelAttribute("cart")
    Cart initializeCart() {
        return new Cart();
    }


    @GetMapping("/cart")
    public String roomPage(Model model, Cart cart) {
		// calculate amount of nights for price calculation
		if(dateHolder.getStartDate()==null || dateHolder.getEndDate()==null){
			return "cart";
		}
		Long diffDays = ChronoUnit.DAYS.between(dateHolder.getStartDate(),dateHolder.getEndDate());

		List<DedicatedRoom> castedCart = cart.map(cartItem -> (DedicatedRoom) cartItem.getProduct()).toList();
		// updating model attributes for output on the template
		model.addAttribute("cart",cart);
		model.addAttribute("castedCart", castedCart);
		model.addAttribute("startDate",dateHolder.getStartDate());
		model.addAttribute("endDate",dateHolder.getEndDate());
		model.addAttribute("diffDays",diffDays);
		model.addAttribute("boardType", boardAndPersonHolder.getBoardType());
		model.addAttribute("personCount", boardAndPersonHolder.getPersonCount());
		return "cart"; // Name of the Thymeleaf-Templates wich has to be rendered
    }

	// Post Mapping to remove items from cart
	@PostMapping("/removeItem")
	public String removeItem(@RequestParam("itemId") String itemId, Cart cart) {
		Product.ProductIdentifier pid = cart.getItem(itemId).get().getProduct().getId();
		cart.removeItem(itemId);
		// Changing and saving the inCart state
		roomCatalog.findById(pid).get().setInCart(Boolean.FALSE);
		roomCatalog.save(roomCatalog.findById(pid).get());
		return "redirect:/cart";
	}

    @PostMapping("/addRoom")
    public String addRoom(@RequestParam("pid") DedicatedRoom room, @ModelAttribute Cart cart,
						  RedirectAttributes redirectAttributes) {
		String errorMessage = null;
		// check if start- and end date are set before adding rooms to cart
		if(dateHolder.getStartDate()==null || dateHolder.getEndDate()==null) {
			errorMessage = "Set your desired start- and end dates.";
			// redirect variable errorMessage to use it in the CatalogController
			redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
			return "redirect:/catalog";
		}

        cart.addOrUpdateItem(room, Quantity.of(1));
		// Changing and saving the in cart state
		room.setInCart(Boolean.TRUE);
		roomCatalog.save(room);
        return "redirect:/catalog"; // Redirecting to the '/rooms' endpoint
    }

    @PostMapping("/checkout")
	@Transactional
    public String buy(@ModelAttribute Cart cart, @LoggedIn UserAccount userAccount, Model model) {

		// Payment Method has to be changed to CreditCard later
		var order = new Order(userAccount.getId(), Cash.CASH);

		// Calculating reservation number (existing reservations + 1)
		Long numberReservation = reservationRepository.findAll().stream().count() + 1;
		// Creating new Reservation
		Reservation reservation = new Reservation("Reservation-"+numberReservation,
			Money.of(0,EURO),new HashSet<>());
		reservation.setReservationNumber(numberReservation);
		// Setting start & end dates of the reservation
		LocalDate startDate = LocalDate.parse(model.getAttribute("startDate").toString());
		LocalDate endDate = LocalDate.parse(model.getAttribute("endDate").toString());
		reservation.setStartDate(startDate);
		reservation.setEndDate(endDate);

		// iterating through the cart and adding dedicated rooms to reservation
		for (CartItem cartItem : cart) {
			reservation.addRoom((DedicatedRoom) cartItem.getProduct());
			DedicatedRoom room = (DedicatedRoom) cartItem.getProduct();
			// adding booked dates to the dedicated room in the catalog
			room.setBookedDates(startDate, endDate);
			// changing the in cart state
			room.setInCart(Boolean.FALSE);
			// saving changes of the room catalog
			roomCatalog.save(room);
		}
		// calculating resulting price of the whole reservation
		reservation.updatePrice();
		// saving order Id in the reservation (needed for easier thymeleaf access)
		reservation.setOrderID(order.getId());

		reservation.setPaymentStatus(PaymentStatus.OPEN);
		// saving reservation in the reservation repository
		reservationRepository.save(reservation);

		// creating an UniqueInventoryItem and adding it to the inventory (needed for order creation)
		reservationInventory.save(new UniqueInventoryItem(reservation,Quantity.of(1)));

		order.addOrderLine(reservation,Quantity.of(1));

		//orderManagement.payOrder(order);
		orderManagement.save(order);
		//orderManagement.completeOrder(order);

		cart.clear();
		return "redirect:/bookings";
	}

    @GetMapping("/bookings")
    public String viewBookings(Model model, @LoggedIn UserAccount userAccount) {

		List<Reservation> reservationList = new ArrayList<>();
		List<Order> listOrders = new ArrayList<>();

		// Adding only active orders to listOrders for showing on website (has to be different for Manager view)
		for (Order order : orderManagement.findBy(userAccount).toList()) {
			if(order.getOrderStatus()!=OrderStatus.CANCELED) {
				listOrders.add(order);
			}
		}

		// Filling reservation list (contains every reservation for the loggedIn User)
		for (Order order : listOrders) {
			for (OrderLine orderLine : order.getOrderLines()) {
				Reservation tempReservation = reservationRepository.findById(orderLine.getProductIdentifier()).get();
				reservationList.add(tempReservation);
			}
		}

		model.addAttribute("reservationList", reservationList);
        return "bookings";
    }

	// PostMapping for paying in advance
	@PostMapping("/payInAdvance")
	public String payInAdvance(@RequestParam("payInAdvance") Order.OrderIdentifier orderId,
	 							@LoggedIn UserAccount userAccount){
		// Change payment status to paid (for orderManagement status & reservation status)
		for (Order order : orderManagement.findBy(userAccount)) {
			if(!order.getId().equals(orderId)) {
				continue;
			}
			orderManagement.payOrder(order);
			reservationRepository.payReservation(orderId);
		}
		return "redirect:/bookings";
	}

	// PostMapping for cancelling an order
    @PostMapping("/cancel")
    public String cancel(@RequestParam("orderId") Order order) {
        // Cancel order in the orderManagement (Salespoint)
        orderManagement.cancelOrder(order,"Guest wanted to cancel the booking.");

		// Set PaymentStatus in the reservation to cancelled + Remove booked Dates from each Dedicated Room in the order
		for (OrderLine orderLine : order.getOrderLines()) {
			// Change reservation status to cancelled
			Reservation tempReservation = reservationRepository.findById(orderLine.getProductIdentifier()).get();
			tempReservation.setPaymentStatus(PaymentStatus.CANCELED);
			reservationRepository.save(tempReservation);

			// Remove booked dates from room catalog
			for(DedicatedRoom room : tempReservation.getRoomSet()){
				room.removeBookedDates(tempReservation.getStartDate(),tempReservation.getEndDate());
				roomCatalog.save(room);
			}
		}
        return "redirect:/bookings";
    }

}
