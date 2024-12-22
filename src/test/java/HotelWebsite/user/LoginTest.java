package HotelWebsite.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;

import HotelWebsite.user.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;



public class LoginTest {
    @BeforeAll
	public static void setUp(){
    }
    @Test
    void testRegistration(){
		LocalDate expirationDate = LocalDate.of(2050, 1, 1);
        RegisteredUserRepository userDataBase = spy(RegisteredUserRepository.class);
        UserAccountManagement userAccounts = spy(UserAccountManagement.class);
        RegisteredUserManagement registeredUserManagement = new RegisteredUserManagement(userDataBase, userAccounts);
        registeredUserManagement.createRegisteredUser(new RegistrationForm("TestUser","123","TestUser", "123123", expirationDate));

        assertNotNull(registeredUserManagement);

    }



    @Test
    void demoTestMethod() {
        assertTrue(true);
    }
}
