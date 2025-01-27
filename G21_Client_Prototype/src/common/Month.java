package common;


public enum Month {
    JANUARY(1, "January"),
    FEBRUARY(2, "February"),
    MARCH(3, "March"),
    APRIL(4, "April"),
    MAY(5, "May"),
    JUNE(6, "June"),
    JULY(7, "July"),
    AUGUST(8, "August"),
    SEPTEMBER(9, "September"),
    OCTOBER(10, "October"),
    NOVEMBER(11, "November"),
    DECEMBER(12, "December");

    private final int monthNumber;
    private final String monthName;

    // Constructor
    Month(int monthNumber, String monthName) {
        this.monthNumber = monthNumber;
        this.monthName = monthName;
    }

    // Getter for month number
    public int getMonthNumber() {
        return monthNumber;
    }

    // Override toString to return the month name
    @Override
    public String toString() {
        return monthName;
    }

    // Static method to get a Month by number
    public static Month getByNumber(int number) {
        for (Month month : Month.values()) {
            if (month.getMonthNumber() == number) {
                return month;
            }
        }
        throw new IllegalArgumentException("Invalid month number: " + number);
    }
    
 // Static method to get a Month by name
    public static Month getByName(String name) {
        for (Month month : Month.values()) {
            if (month.name().equalsIgnoreCase(name) || month.toString().equalsIgnoreCase(name)) {
                return month;
            }
        }
        throw new IllegalArgumentException("Invalid month name: " + name);
    }
}

