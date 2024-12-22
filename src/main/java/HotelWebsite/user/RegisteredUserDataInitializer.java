package HotelWebsite.user;

import com.mysema.commons.lang.Assert;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Order(10)
class RegisteredUserDataInitializer implements DataInitializer {

	private final UserAccountManagement userAccountManagement;

	private final RegisteredUserManagement registeredUserManagement;

	RegisteredUserDataInitializer(UserAccountManagement userAccountManagement,
								  RegisteredUserManagement registeredUserManagement){
		Assert.notNull(userAccountManagement, "UserAccountManagement must not be null!");
		Assert.notNull(registeredUserManagement, "RegisteredUserManagement must not be null!");

		this.userAccountManagement = userAccountManagement;
		this.registeredUserManagement = registeredUserManagement;
	}

	@Override
	public void initialize() {
		if (userAccountManagement.findByUsername("Manager").isPresent()) {
			return;
		}

		userAccountManagement.create("Manager", Password.UnencryptedPassword.of("123"), Role.of("MANAGER"));
		registeredUserManagement.createStaffMember("Adrian Feller", "123", "Callcenter");
		registeredUserManagement.createStaffMember("Oliver Pohl", "123", "Cleaning");
		registeredUserManagement.createStaffMember("Antonia Groß", "123", "Cleaning");
		registeredUserManagement.createStaffMember("Bastian Schunk", "123", "Cleaning");

		LocalDate expirationDate = LocalDate.of(2050, 1, 1);

		registeredUserManagement.createRegisteredUser(new RegistrationForm("TestUser","123","TestUser", "123123", expirationDate));

	}
}
