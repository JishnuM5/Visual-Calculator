
import java.util.Scanner;

public class Cell implements Comparable<Cell> {
	public int column;
	public int row;
	public String printVal;

	// Constructor: sets the shown value, and breaks name into row and column
	public Cell(String printVal, String coord) {

		this.printVal = printVal;
		// Explicit Casting for Row, Wrapper Method for Column
		column = (int) coord.charAt(0) - 65;
		row = Integer.parseInt(coord.substring(1)) - 1;
	}

	// Coordinate Validity: checks if given coordinates for a cell are valid
	// Not Relevant to an Instance so Static Method
	public static boolean validCoord(String coord) {
		if (coord.charAt(0) >= 'A' && coord.charAt(0) <= 'Z') {
			// If first char of coord is valid, use scanner to check for int
			Scanner checkNum = new Scanner(coord.substring(1));
			if (checkNum.hasNextInt()) {
				return true;
			}
		}
		return false;
	}

	@Override
	// Compare: Comparing Two Cells Based on Their Print Values
	public int compareTo(Cell o) {
		return printVal.compareTo(o.printVal);
	}
}
