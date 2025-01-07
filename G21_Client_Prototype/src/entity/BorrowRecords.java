package entity;

import java.sql.Date;

public class BorrowRecords {
	private String bookBarcode;
	private String bookTitle;
	private int bookcopyNo;
	private Date borrowActualReturnDate;
	private Date borrowDate;
	private Date borrowExpectReturnDate;
	private int borrowLostBook;
	private int borrowNumber;
	private int librarianId;
	private String librarianName;
	private int subscriberId;
	
	public BorrowRecords(String bookBarcode, String bookTitle, int bookcopyNo, Date borrowActualReturnDate,
			Date borrowDate, Date borrowExpectReturnDate, int borrowLostBook, int borrowNumber, int librarianId,
			String librarianName, int subscriberId) {
		this.bookBarcode = bookBarcode;
		this.bookTitle = bookTitle;
		this.bookcopyNo = bookcopyNo;
		this.borrowActualReturnDate = borrowActualReturnDate;
		this.borrowDate = borrowDate;
		this.borrowExpectReturnDate = borrowExpectReturnDate;
		this.borrowLostBook = borrowLostBook;
		this.borrowNumber = borrowNumber;
		this.librarianId = librarianId;
		this.librarianName = librarianName;
		this.subscriberId = subscriberId;
	}
	
	public BorrowRecords(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	public Date getBorrowActualReturnDate() {
		return borrowActualReturnDate;
	}

	public void setBorrowActualReturnDate(Date borrowActualReturnDate) {
		this.borrowActualReturnDate = borrowActualReturnDate;
	}

	public Date getBorrowExpectReturnDate() {
		return borrowExpectReturnDate;
	}

	public void setBorrowExpectReturnDate(Date borrowExpectReturnDate) {
		this.borrowExpectReturnDate = borrowExpectReturnDate;
	}

	public int getBorrowLostBook() {
		return borrowLostBook;
	}

	public void setBorrowLostBook(int borrowLostBook) {
		this.borrowLostBook = borrowLostBook;
	}

	public int getBorrowNumber() {
		return borrowNumber;
	}

	public void setBorrowNumber(int borrowNumber) {
		this.borrowNumber = borrowNumber;
	}

	public int getLibrarianId() {
		return librarianId;
	}

	public void setLibrarianId(int librarianId) {
		this.librarianId = librarianId;
	}

	public String getLibrarianName() {
		return librarianName;
	}

	public void setLibrarianName(String librarianName) {
		this.librarianName = librarianName;
	}

	public String getBookBarcode() {
		return bookBarcode;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public int getBookcopyNo() {
		return bookcopyNo;
	}

	public Date getBorrowDate() {
		return borrowDate;
	}

	public int getSubscriberId() {
		return subscriberId;
	}

	@Override
	public String toString() {
		return "BorrowRecords [bookBarcode=" + bookBarcode + ", bookTitle=" + bookTitle + ", bookcopyNo=" + bookcopyNo
				+ ", borrowActualReturnDate=" + borrowActualReturnDate + ", borrowDate=" + borrowDate
				+ ", borrowExpectReturnDate=" + borrowExpectReturnDate + ", borrowLostBook=" + borrowLostBook
				+ ", borrowNumber=" + borrowNumber + ", librarianId=" + librarianId + ", librarianName=" + librarianName
				+ ", subscriberId=" + subscriberId + "]";
	}
	
	
	
	
}