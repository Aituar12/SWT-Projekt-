package HotelWebsite.user;

import org.salespointframework.useraccount.UserAccount;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.io.Serializable;

import org.jmolecules.ddd.types.Identifier;
import org.salespointframework.core.AbstractAggregateRoot;
import java.util.UUID;
import HotelWebsite.user.RegisteredUser.UserIdentifier;


@Entity
public class RegisteredUser extends AbstractAggregateRoot<UserIdentifier> {
    
   	private @EmbeddedId UserIdentifier id = new UserIdentifier();

	@OneToOne
    private UserAccount userAccount;
    private String creditcardnumber;
	private String cardholderName;
	private String department;

    public RegisteredUser(){
        //Default Constructor
    }
	//Customer Constructor
    public RegisteredUser(UserAccount userAccount, String creditcardnumber, String cardholderName){
        this.userAccount = userAccount;
        this.creditcardnumber = creditcardnumber;
		this.cardholderName = cardholderName;
    }

	//Staff Constructor
	public RegisteredUser(UserAccount userAccount, String department){
		this.userAccount = userAccount;
		this.department = department;
	}

	public String getCardholderName(){
		return this.cardholderName;
	}

    public String getCreditCardNumber(){
        return this.creditcardnumber;
    }

    public void setCreditCardNumber(String creditcardnumber){
        this.creditcardnumber = creditcardnumber;
    }

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

    public UserAccount getUserAccount(){
        return this.userAccount;
    }

    @Override
    public UserIdentifier getId(){
        return id;          
    }

    @Embeddable
	public static final class UserIdentifier implements Identifier, Serializable {

		private static final long serialVersionUID = 7740660930809051850L;
		private final @SuppressWarnings("unused") UUID identifier;

		/**
		 * Creates a new unique identifier for {@link Customer}s.
		 */
		UserIdentifier() {
			this(UUID.randomUUID());
		}

		/**
		 * Only needed for property editor, shouldn't be used otherwise.
		 *
		 * @param identifier The string representation of the identifier.
		 */
		UserIdentifier(UUID identifier) {
			this.identifier = identifier;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;

			result = prime * result + (identifier == null ? 0 : identifier.hashCode());

			return result;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {

			if (obj == this) {
				return true;
			}

			if (!(obj instanceof UserIdentifier that)) {
				return false;
			}

			return this.identifier.equals(that.identifier);
		}
	}
}
