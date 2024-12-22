package HotelWebsite.Management;


import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.core.Currencies;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StatisticTest {
	private static Statistic statistic;
	private static List<TransactionEntry> mockList;
	private static TransactionEntry tooLongAgo;

	@BeforeAll
	static void setUp() {

		mockList = new ArrayList<>();

		StatisticRepository repo = mock(StatisticRepository.class);
		// mock behavior of statistic savin entry
		when(repo.save(any(TransactionEntry.class))).then(invocation -> {
			TransactionEntry entry = invocation.getArgument(0);
			mockList.add(entry);
			return entry;
		});

		statistic = new Statistic(repo);
		when(statistic.getTransactions()).then(invocation -> {
			return mockList;
		});


		//These should be Listed in the transactions for yesterday
		LocalDate today = LocalDate.now();
		statistic.createEntry(25, today);
		LocalDate yesterday = LocalDate.now().minusDays(1);
		statistic.createEntry(3, yesterday);
		statistic.createEntry(-20, yesterday);
		statistic.createEntry(10, yesterday);
		statistic.createEntry(-3, yesterday);

		//These should be listed in the transactions up until one week ago
		LocalDate sevenDaysago = LocalDate.now().minusDays(7);
		LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
		LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
		statistic.createEntry(30, sevenDaysago);
		statistic.createEntry(-20, sevenDaysago);
		statistic.createEntry(100, twoDaysAgo);
		statistic.createEntry(-10, twoDaysAgo);
		statistic.createEntry(710, oneWeekAgo);
		statistic.createEntry(-93, oneWeekAgo);

		// Transactions up until one month ago
		LocalDate eightDaysAgo = LocalDate.now().minusDays(8);
		LocalDate threeWeeksAgo = LocalDate.now().minusWeeks(3);
		LocalDate twentyDaysAgo = LocalDate.now().minusDays(20);
		statistic.createEntry(99, eightDaysAgo);
		statistic.createEntry(-5, eightDaysAgo);
		statistic.createEntry(20, threeWeeksAgo);
		statistic.createEntry(55, threeWeeksAgo);
		statistic.createEntry(80, twentyDaysAgo);
		statistic.createEntry(-10, twentyDaysAgo);

		//This should not be listed anywhere
		LocalDate thirtytwoDaysAgo = LocalDate.now().minusDays(32);
		tooLongAgo = statistic.createEntry(666666, thirtytwoDaysAgo);
	}

	@Test
	void statisticSetupRepoNullTest() {
		assertThrows(Exception.class, () -> {
			Statistic emptyStatistic = new Statistic(null);
		});
	}

	@Test
	void allTransactionsAddedTest() {
		assertEquals(18, statistic.getTransactions().size());
	}

	@Test
	void getRevenueTodayTest() {
		int expectedSize = 1;
		int daysAgo = 0;
		List<TransactionEntry> result = statistic.getRevenue(daysAgo);
		System.out.println(result);
		// Check if they have the same size
		assertEquals(expectedSize, result.size(), "Expected size doesn't match actual size");
		// Check that it is considered revenue
		assertTrue((statistic.getRevenue(0).get(0)).isRevenue());
		// Check that it has the right amount
		assertEquals(25, (statistic.getRevenue(0).get(0).getAmount()));
	}

	@Test
	void getRevenueYesterdayTest() {
		int expectedSize = 3;
		int daysAgo = 1;
		List<TransactionEntry> result = statistic.getRevenue(daysAgo);
		System.out.println(result);
		assertEquals(expectedSize, result.size(), "Expected size doesn't match actual size");
	}

	@Test
	void getRevenueLastWeekTest() {
		int expectedSize = 6;
		int daysAgo = 7;
		List<TransactionEntry> result = statistic.getRevenue(daysAgo);
		System.out.println(result);
		assertEquals(expectedSize, result.size(), "Expected size doesn't match actual size");
	}

	@Test
	void getExpensesLastWeekTest() {
		int expectedSize = 5;
		int daysAgo = 7;
		List<TransactionEntry> result = statistic.getExpenses(daysAgo);
		System.out.println(result);
		assertEquals(expectedSize, result.size(), "Expected size doesn't match actual size");
	}

	@Test
	void oldEntriesNotAddedTest() {
		int daysAgo = 30;
		List<TransactionEntry> result = statistic.getRevenue(daysAgo);
		assertFalse(result.contains(tooLongAgo));
	}

	@Test
	public void entryWithAmountZero() {
		assertThrows(AssertionError.class, () -> {
				statistic.createEntry(0, LocalDate.now());
			}
		);

	}
}
