package HotelWebsite.Management;

import org.springframework.util.Assert;

import org.salespointframework.core.DataInitializer;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * The Statistics data initializer creates default {@link TransactionEntry}s .
 * @author Celina Stransky
 */
@Component
class StatisticsDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(StatisticsDataInitializer.class);
	private final Statistic statistic;

	/**
	 * Instantiates a new Statistics data initializer.
	 *
	 * @param statistic the statistic
	 */
	public StatisticsDataInitializer(Statistic statistic){
		Assert.notNull(statistic, "Statistic must not be null");
		this.statistic = statistic;
		LOG.info("statistic successfully created");
	}

	/**
	 * populate our database with {@link TransactionEntry}s
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {

		LOG.info("Creating default statistic entries");

		// Creating some Date entries
		LocalDate today = LocalDate.now();
		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDate sevenDaysago = LocalDate.now().minusDays(7);
		LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
		LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
		LocalDate eightDaysAgo = LocalDate.now().minusDays(8);
		LocalDate threeWeeksAgo = LocalDate.now().minusWeeks(3);
		LocalDate twentyDaysAgo = LocalDate.now().minusDays(20);
		LocalDate thirtytwoDaysAgo = LocalDate.now().minusDays(32);
		//These should be Listed in the transactions for yesterday
		statistic.createEntry(25, today);
		statistic.createEntry(3, yesterday);
		statistic.createEntry(-20, yesterday);
		statistic.createEntry(10, yesterday);
		statistic.createEntry(-3, yesterday);
		//These should be listed in the transactions up until one week ago
		statistic.createEntry(30, sevenDaysago);
		statistic.createEntry(-20, sevenDaysago);
		statistic.createEntry(100, twoDaysAgo);
		statistic.createEntry(-10, twoDaysAgo);
		statistic.createEntry(710, oneWeekAgo);
		statistic.createEntry(-93, oneWeekAgo);
		// Transactions up until one month ago
		statistic.createEntry(99, eightDaysAgo);
		statistic.createEntry(-5, eightDaysAgo);
		statistic.createEntry(20, threeWeeksAgo);
		statistic.createEntry(55, threeWeeksAgo);
		statistic.createEntry(80, twentyDaysAgo);
		statistic.createEntry(-10, twentyDaysAgo);
		//This should not be listed anywhere
		statistic.createEntry(666666, thirtytwoDaysAgo);
	}
}
