package HotelWebsite.user;

import org.salespointframework.useraccount.Password.UnencryptedPassword;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;


@Service
@Transactional
public class RegisteredUserManagement{

    public static final Role REGISTERED_USER_ROLE = Role.of("REGISTERED_USER");
    public static final Role STAFF_ROLE = Role.of("STAFF");

    private final RegisteredUserRepository registeredUser;
    private final UserAccountManagement userAccounts;

    RegisteredUserManagement(RegisteredUserRepository users, UserAccountManagement userAccounts){
        Assert.notNull(users, "RegisteredUserRepository must not be null");
        Assert.notNull(userAccounts, "UserAccountManagement must not be null!" );

        this.registeredUser = users;
        this.userAccounts = userAccounts;
    }

    public RegisteredUser createRegisteredUser(RegistrationForm form){

        Assert.notNull(form, "Registration form must not be null!");

        var password = UnencryptedPassword.of(form.getPassword());
        var userAccount = userAccounts.create(form.getName(), password, REGISTERED_USER_ROLE);

        return registeredUser.save(new RegisteredUser(userAccount, form.getCreditCardNumber(),
			form.getCardholderName()));
    }

    public RegisteredUser createStaffMember(String name, String password, String department){
        Assert.notNull(department, "The Department must not be null!");

        var given_password = UnencryptedPassword.of(password);
        var userAccount = userAccounts.create(name, given_password, STAFF_ROLE);

        return registeredUser.save(new RegisteredUser(userAccount, department));
    }

    public Streamable<RegisteredUser> findall(){
        return registeredUser.findAll();
    }

	public List<RegisteredUser> findAllByDepartment(String department) {
		return registeredUser.findAll()
			.filter(user -> user.getDepartment() != null)
			.filter(user -> user.getDepartment().equals(department))
			.toList();
	}
}