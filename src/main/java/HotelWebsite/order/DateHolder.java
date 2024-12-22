package HotelWebsite.order;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

// DateHolder class is used to call up the currently set start and end date in all controllers
@Component
public class DateHolder {
	private LocalDate startDate = null;
	private LocalDate endDate = null;

	public LocalDate getStartDate() { return startDate;}

	public LocalDate getEndDate() { return endDate;}

	public void setStartDate(LocalDate startDate) { this.startDate = startDate;}

	public void setEndDate(LocalDate endDate) { this.endDate = endDate;}
}
