
import java.util.Scanner;
import java.math.BigDecimal;

public class FormulaCell extends Cell {
	public double value;
	// Whether formula (in printVal) references cell
	public boolean hasCoord = false;
	// Whether formula is erroneous
	public boolean hasError = false;
	// The original SUM/AVG formula for retrieval
	public String sumAvgForm;
	// An array of values to turn Cell into BigFormulaCell when needed
	public String[] BFCVals = new String[3];

	// Number Constructor: super + int value
	public FormulaCell(double value, String coord) {
		super(value + "", coord);
		this.value = value;
	}

	// BigDecimal Constructor: super + BigDecimal in Subclass
	public FormulaCell(String BigDecimal, String coord) {
		super(BigDecimal, coord);
	}

	// SUM/AVG Formula Constructor: Formula COnstructor + Original Command
	public FormulaCell(String formula, String coord, boolean showCalc, String sumAvgForm) {
		this(formula, coord, showCalc);
		this.sumAvgForm = sumAvgForm;
	}

	// Formula Constructor: super + calculated value
	public FormulaCell(String formula, String coord, boolean showCalc) {
		super(formula, coord);
		formula = coordtoValue(formula).trim();

		if (hasCoord && !formula.contains(" ")) {
			// If coordinate just references another cell, don't need to calculate
			Scanner scan = new Scanner(formula);
			String scanVal = scan.next();
			if (!scanVal.equals("X")) {
				double val = Double.parseDouble(scanVal);
				if ((val + "").equals("Infinity")) {
					BFCVals[0] = printVal;
					BFCVals[1] = formula;
					BFCVals[2] = coord;
				} else {
					this.value = Double.parseDouble(formula);
				}
			} else {
				System.out.println("FormulaCell Error: reference to invalid cell");
				printVal += " (has error)";
				hasError = true;
			}
			scan.close();
		} else {
			// Else, send formula to calculate, gives error (cell cleared) or answer

			if (hasError) {
				// If there are errors in the formula, don't show calculations
				showCalc = false;
			}

			if (formula.contains("X")) {
				// If g.getFromGrid puts an X in in FormulaCell, an invalid cell was referenced
				// The formula will not be processed
				System.out.println("FormulaCell Error: reference to invalid cell");
				this.printVal += " (has error)";
				hasError = true;
			} else {
				String answer = DecCalc.processInput(formula, showCalc);
				if (showCalc) {
					System.out.println();
				}

				// Processed Answer is a Value, BigDecimal, or Error
				if (answer.contains("Error")) {
					System.out.println("FormulaCell " + answer);
					this.printVal += " (has error)";
					hasError = true;
				} else {
					Scanner scan = new Scanner(answer);
					String scanVal = scan.next();
					double val = Double.parseDouble(scanVal);
					if ((val + "").equals("Infinity")) {
						// Creating Array of Values to Be Accessed by BigFormulaCell Constructor
						BFCVals[0] = printVal;
						BFCVals[1] = answer;
						BFCVals[2] = coord;
					} else {
						this.value = Double.parseDouble(answer);
					}
					scan.close();
				}
			}
		}
	}

	// Coordinate Conversion: all instances of cell references replaced with values
	public String coordtoValue(String formula) {
		Scanner scan = new Scanner(formula);
		while (scan.hasNext()) {
			String val = scan.next();

			// Scanner and String Methods to Check for and Replace Cell References
			if (Cell.validCoord(val)) {
				// Updating All Cells Till Referenced Cell
				hasError = VisiCalc.g.updateValues(Integer.parseInt(val.substring(1)) - 1, (int) val.charAt(0) - 65);
				String replaceVal = VisiCalc.g.getFromGrid(val, 'v');
				if (replaceVal.contains("Error")) {
					formula = formula.replace(val, "X");
				} else {
					formula = formula.replace(val, replaceVal);
				}

				hasCoord = true;

			}
		}
		scan.close();
		return formula;
	}

	@Override
	// Compare Override: Comparing Two FormulaCells Based on Final Values
	public int compareTo(Cell o) {
		// If Statements Considering Different Possibilities for BigFormulaCells
		if (this instanceof BigFormulaCell && o instanceof BigFormulaCell) {
			BigFormulaCell big = (BigFormulaCell) this;
			BigFormulaCell oBig = (BigFormulaCell) o;
			return big.bigValue.compareTo(oBig.bigValue);
		} else if (this instanceof BigFormulaCell) {
			BigFormulaCell big = (BigFormulaCell) this;
			FormulaCell oBig = (FormulaCell) o;
			return big.bigValue.compareTo(BigDecimal.valueOf(oBig.value));
		} else if (o instanceof BigFormulaCell) {
			FormulaCell big = (FormulaCell) this;
			BigFormulaCell oBig = (BigFormulaCell) o;
			return BigDecimal.valueOf(big.value).compareTo(oBig.bigValue);
		} else {
			FormulaCell oFC = (FormulaCell) o;
			return Double.compare(this.value, oFC.value);
		}
	}
}
