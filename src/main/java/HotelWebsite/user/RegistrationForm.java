package HotelWebsite.user;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.validation.Errors;

import java.time.LocalDate;

public class RegistrationForm {

	@NotEmpty
	@Size(min = 3, max = 40)
	private final String name, cardholderName;
	@NotEmpty
	@Size(min = 8, max = 40)
	private final String password;

	@NotEmpty
	@Size(min = 12, max = 16)
	private final String creditCardNumber;

	@Future
	private final LocalDate expirationDate;

	public RegistrationForm(String name, String password, String cardholderName, String creditCardNumber, LocalDate expirationDate) {

		this.name = name;
		this.password = password;
		this.cardholderName = cardholderName;
		this.creditCardNumber = creditCardNumber;
		this.expirationDate = expirationDate;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getCardholderName() {
		return cardholderName;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public LocalDate getExpirationDate() {
		return expirationDate;
	}
}
