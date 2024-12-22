package HotelWebsite.order;

import org.springframework.stereotype.Component;

import HotelWebsite.order.Reservation.Board;


// DateHolder class is used to call up the currently set start and end date in all controllers
@Component
public class BoardAndPersonHolder {
	private Integer personCount = 0;
	private Board boardType = null;

	public Board getBoardType() {
        return boardType;
    }

    public Integer getPersonCount() {
        return personCount;
    }

    public void setBoardType(Board boardType) {
        this.boardType = boardType;
    }

    public void setPersonCount(Integer personCount) {
        this.personCount = personCount;
    }
}
