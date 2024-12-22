package HotelWebsite.Management;

import jakarta.persistence.*;
import org.jmolecules.ddd.types.Identifier;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * TransactionEntries are created whenever money flows (e.g. cancelling reservation, pay reservation etc.)
 */
@Entity
public class TransactionEntry implements Comparable<TransactionEntry>{

	private final @EmbeddedId TransactionIdentifier id = new TransactionIdentifier();
	private LocalDate date;
	private double amount;


	/**
	 * Instantiates a new unique Transaction entry.
	 *
	 * @param amount the amount
	 * @param date  the date
	 */
	public TransactionEntry(double amount, LocalDate date) {
		this.date = date;
		this.amount = amount;
	}

	/**
	 * Instantiates a new Transaction entry.
	 * Needed because {@link Entity} needs a no-arg Constructor.
	 * Is private, because it should not be accessed from outside
	 */
	@SuppressWarnings("unused")
	protected TransactionEntry() {
	}

	/**
	 * Get amount double.
	 * @return the double
	 */
	public double getAmount(){return amount;}

	/**
	 * Gets formatted date.
	 * @return the formatted date as String
	 */
	public String getDate() {
		// Format the LocalDate as a string using a DateTimeFormatter
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM uuuu");
		return date.format(formatter);
	}

	/**
	 * Gets date.
	 * @return the local date as LocalDate
	 */
	public LocalDate getLocalDate() {
		return date;
	}

	@Override
	public String toString() {
		return "EUR " + amount + " " + date.toString();
	}

	/**
	 * Is revenue boolean.
	 * @return the boolean
	 */
	public boolean isRevenue(){
		return amount >0;
	}

	/**
	 * Is expense boolean.
	 * @return the boolean
	 */
	public boolean isExpense(){
		return amount<0;
	}

	@Override
	public int compareTo(TransactionEntry obj) {
		int yearDiff = this.date.getYear()-obj.date.getYear();
		int dayDiff = this.date.getDayOfYear() - obj.date.getDayOfYear();
		return yearDiff*100 + dayDiff;
	}

	@Override
	public boolean equals(Object obj){
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TransactionEntry that)) {
			return false;
		}
		return this.id.equals(that.id);
	}

	public int hashCode(){
		return this.date.getYear()
			+this.date.getMonthValue()
			+this.date.getDayOfYear()
			+this.id.hashCode();
	}

	/**
	 * The type Transaction identifier.
	 */
	@Embeddable
	public static final class TransactionIdentifier implements Identifier, Serializable {
		private final UUID identifier;

		/**
		 * Instantiates a new unique Transaction identifier for {@link TransactionEntry}.
		 */
		public TransactionIdentifier(){
			this(UUID.randomUUID());
		}

		/**
		 * Only needed for property editor, shouldn't be used otherwise.
		 *
		 * @param identifier The string representation of the identifier.
		 */
		public TransactionIdentifier(UUID identifier){
			this.identifier = identifier;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (identifier == null ? 0 : identifier.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof TransactionIdentifier that)) {
				return false;
			}
			return this.identifier.equals(that.identifier);
		}
	}

}