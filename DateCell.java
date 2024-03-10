
import java.util.Scanner;

public class DateCell extends Cell {
	public int value;

	// Constructor: super + int value as YYYYMMDD
	public DateCell(String printVal, String coord) {
		super(printVal, coord);

		if (printVal.charAt(1) == '/') {
			printVal = "0" + printVal;
		}
		if (printVal.charAt(4) == '/') {
			printVal = printVal.substring(0, 3) + "0" + printVal.substring(3);
		}
		int l = printVal.length();
		for (int i = 0; i < 10 - l; i++) {
			printVal = printVal.substring(0, 6) + "0" + printVal.substring(6);
		}
		
		value = Integer.parseInt(printVal.replace("/", ""));

		// Modulus and /10 to Reformat Date
		int year = (value % 10000) * 10000;
		int month = (value % 1000000) / 10000 * 100;
		int day = value / 1000000;
		value = year + month + day;
		// Debugging: System.out.println(year + " " + month + " " + day + " ");
	}

	// Date Validity: checks whether a date input
	public static boolean validDate(String date) {

		// Must Contain Only 2 "/"
		date = date.replaceFirst("/", " ");
		if (date.contains("/")) {
			date = date.replaceFirst("/", " ");
		} else {
			return false;
		}
		if (date.contains("/")) {
			return false;
		}
		// There Should Be 3 Ints Around Each "/"
		// First 2 Ints are 2-Digit, Next is 4-Digit
		Scanner scan = new Scanner(date);
		for (int i = 0; i < 2; i++) {
			if (!scan.hasNextInt()) {
				scan.close();
				return false;
			} else {
				String num = scan.next();
				if (num.length() > 2) {
					scan.close();
					return false;
				}
			}

		}

		if (!scan.hasNextInt()) {
			scan.close();
			return false;
		} else {
			String num = scan.next();
			if (num.length() > 4) {
				scan.close();
				return false;
			}
		}
		scan.close();
		return true;

	}

	@Override
	// Compare Override: Comparing Two DateCells Based on Temporal Extent
	public int compareTo(Cell o) {
		DateCell oDC = (DateCell) o;
		return this.value - oDC.value;
	}

}