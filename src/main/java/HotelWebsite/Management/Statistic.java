package HotelWebsite.Management;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.Assert.isTrue;

/**
 * Implementation of logic related to the statistics
 * @author Celina Stransky
 */
@Component
public class Statistic {
	private final StatisticRepository repository;


	/**
	 * Instantiates a new {@link Statistic} with a given {@link StatisticRepository}
	 *
	 * @param repository must not be {@literal null}
	 */
	public Statistic(StatisticRepository repository){
		Assert.notNull(repository, "Repository shall not be null!");
		this.repository = repository;
	}

	/**
	 * Create entry transaction entry and save it in the database
	 *
	 * @param amount the amount, can not be 0
	 * @param date   the date, can not be {@literal null}
	 * @return the newly created transaction entry
	 */
	public TransactionEntry createEntry(double amount, LocalDate date){
		Assert.notNull(date, "Date can't be null");
		TransactionEntry entry = new TransactionEntry(amount, date);
		try {
			assert (entry.isRevenue() || entry.isExpense());
		} catch (Exception e) {
			return null;
		}
		return repository.save(entry);
	}

	/**
	 * Gets filtered revenue from {@param daysAgo} days
	 *
	 * @param daysAgo the days ago
	 * @return the revenues as List
	 */
	public List<TransactionEntry> getRevenue(int daysAgo) {
		List<TransactionEntry> transactions = new ArrayList<>();
		repository.findAll().forEach(transactions::add);
		List<TransactionEntry> res = new ArrayList<>();
		LocalDate targetDate = LocalDate.now().minusDays(daysAgo);
		for (TransactionEntry entry : transactions) {
			LocalDate entryDate = entry.getLocalDate();
			if (entry.isRevenue() && (entryDate.isAfter(targetDate) || entryDate.isEqual(targetDate))) {
				res.add(entry);
			}
		}
		Collections.sort(res);
		Collections.reverse(res);
		return res;
	}

	/**
	 * Gets filtered expenses from {@param daysAgo} days
	 *
	 * @param daysAgo the days ago
	 * @return the expenses as List
	 */
	public List<TransactionEntry> getExpenses(int daysAgo) {
		List<TransactionEntry> transactions = new ArrayList<>();
		repository.findAll().forEach(transactions::add);
		List<TransactionEntry> res = new ArrayList<>();
		LocalDate targetDate = LocalDate.now().minusDays(daysAgo);
		for (TransactionEntry entry : transactions) {
			LocalDate entryDate = entry.getLocalDate();
			if (entry.isExpense() && (entryDate.isAfter(targetDate) || entryDate.isEqual(targetDate))) {
				res.add(entry);
			}
		}
		Collections.sort(res);
		Collections.reverse(res);
		return res;
	}

	/**
	 * Get transactions list.
	 *
	 * @return the list
	 */
	public List<TransactionEntry> getTransactions(){
		List<TransactionEntry> transactions = new ArrayList<>();
		repository.findAll().forEach(transactions::add);
		return transactions;
	}
}
