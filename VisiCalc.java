
//VisiCalc
//Jishnu Mehta
//AP Computer Science
//Period 7

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class VisiCalc {
	static final Grid g = new Grid();
	static ArrayList<String> commands = new ArrayList<String>();

	// Main Method: takes input, prints greetings
	public static void main(String[] args) throws FileNotFoundException {
		Scanner console = new Scanner(System.in);
		System.out.println("Welcome to VisiCalc!");
		String expr = "";

		// loop that runs until user says "quit"
		while (!expr.equalsIgnoreCase("quit")) {
			System.out.print("Enter: ");
			expr = console.next();

			if (expr.equalsIgnoreCase("quit")) {
				// If the expression is quit, console prints farewell
				// expression = quit, next test will break loop
				System.out.print("Good Bye!");
			} else if (expr.equalsIgnoreCase("save")) {
				// Else if expression is save, then saves file with given name
				String fileName = console.next();
				File f = new File(fileName);
				PrintStream fileWriter = new PrintStream(f);

				while (!commands.isEmpty()) {
					String input = commands.remove(0);
					fileWriter.println(input);
				}
				System.out.println(fileName + " saved");
				fileWriter.close();
			} else if (expr.equalsIgnoreCase("load")) {
				// Else if expression is load, the loads file with given name
				String fileName = console.next();
				File f = new File(fileName);
				Scanner fileReader = new Scanner(f);

				// Processes inputs and saves them to commands array
				while (fileReader.hasNextLine()) {
					String input = fileReader.nextLine().trim();
					System.out.println("`" + input + "` processing...");
					commands.add(input);
					processInput(input);
					System.out.println();
				}
				fileReader.close();
			} else {
				// Else whole line processed; only these commands saved to commands ArrayList
				expr += console.nextLine();
				commands.add(expr);
				processInput(expr);
			}
			System.out.println();
		}
		console.close();
	}

	// Processing Input: word commands or parsing input, included in commands array
	public static void processInput(String input) {
		if (input.equalsIgnoreCase("help")) {
			// If the input is "help", prints help command
			System.out.println("**************************************************");
			System.out.println("Hello! This is VisiCalc, a simple spreadsheet program");
			System.out.println("There are 7 columns (A-G) and 10 rows (1-10) of cells");
			System.out.println("A <cell>  is refereced as <col><row>");
			System.out.println("Setting a Cell  : <cell> = \" str \", (D)D/(M)M/YYYY, <num>, <formula>");
			System.out.println("Setting Formulas: <cell> = <num>, <cell> <+-*/^> <num>. <cell>");
			System.out.println("Setting Formulas: can use cells and numbers, expressions in ()");
			System.out.println("Data Formulas   : <cell> = (for μ & Σ) SUM, AVG, SORTA, SORTD <cell> - <cell>");
			System.out.println("**************************************************");
		} else if (input.equalsIgnoreCase("print")) {
			// Else if the input is to print, updates values and prints grid g
			g.updateValues(-1, -1);
			g.printGrid();

		} else if (input.length() >= 5 && input.substring(0, 5).equalsIgnoreCase("clear")) {
			// Else if the input is to clear, calls clear method in grid
			g.clear(input);
		} else if (input.length() >= 5 && input.substring(0, 4).equalsIgnoreCase("SORT")) {
			// Else if the input is to sort, call clear method in grid
			if (input.substring(4, 5).equalsIgnoreCase("A") || input.substring(4, 5).equalsIgnoreCase("D")) {
				// Has to be SORTA... or SORTD...
				calcFormCmd(null, input, true);
			} else {
				System.out.println("Error: invalid input command");
			}
		} else {
			// Else sends input to be parsed
			inputParser(input);
		}
	}

	// Parsing Input: using a scanner to break input into token strings
	public static void inputParser(String input) {
		Scanner scanInput = new Scanner(input);

		if (scanInput.hasNext()) {
			String coord = scanInput.next();
			// Conditionals that determine input type
			if (scanInput.hasNext()) {
				// If there are tokens after coordinates, check data type
				if (scanInput.next().equals("=") && scanInput.hasNext()) {
					if (Cell.validCoord(coord)) {
						if (scanInput.hasNextDouble()) {
							// If next token is a num, makes new FormulaCell
							String scanVal = scanInput.next();
							if (scanInput.hasNext()) {
								System.out.println("Error: invalid input command");
							} else {
								double value = Double.parseDouble(scanVal);
								if ((value + "").equals("Infinity")) {
									g.addToGrid(new BigFormulaCell(scanVal, coord));
								} else {
									g.addToGrid(new FormulaCell(value, coord));
								}
							}
						} else {
							String cellID = scanInput.next();
							// Else looks at next token to determine cell type
							if (scanInput.hasNext() || cellID.contains("/")) {
								if (cellID.equals("\"")) {
									String words = scanInput.nextLine();
									if (words.length() > 2 && (words.substring(words.length() - 2).equals(" \""))) {
										// text has to be at least "" ""
										g.addToGrid(new TextCell(words.substring(1, words.length() - 2), coord));
									} else {
										System.out.println("Error: invalid text input");
									}

								} else if (cellID.equals("(")) {
									String form = scanInput.nextLine().trim();
									if (form.length() > 2 && (form.substring(form.length() - 2).equals(" )"))) {
										// formula have to be at least "( )" and cannot be blank

										String sub4 = (form.length() > 3) ? form.substring(0, 4) : "";
										if (sub4.equalsIgnoreCase("AVG ") || sub4.equalsIgnoreCase("SUM ")) {
											// If formula starts w/ SUM/AVG, call calcFormCmd method
											calcFormCmd(coord, form.substring(0, form.length() - 2).trim(), false);
										} else {
											// Else create a new FormulaCell with a formula expression
											FormulaCell fc = new FormulaCell(form.substring(0, form.length() - 2),
													coord, true);
											// Checking Whether Calculation Gives a BigDecimal or Not
											if (fc.BFCVals[0] == null) {
												// If BigDecimal array not updated, add FormulaCell to grid
												g.addToGrid(fc);
											} else {
												// Else create new BigFormulaCell with BigDecimal array, add to grid
												g.addToGrid(new BigFormulaCell(fc.BFCVals[0], fc.BFCVals[1],
														fc.BFCVals[2], fc.hasCoord));
											}
										}
									} else {
										System.out.println("Error: invalid formula input");
									}

								} else if (cellID.contains("/")) {
									// Else if the cellID has a "/" assume DateCell, validate, and create
									if (DateCell.validDate(cellID)) {
										g.addToGrid(new DateCell(cellID, coord));
									} else {
										System.out.println("Error: invalid date input");
									}

								} else {
									// Else token doesn't match any cell type, give error
									System.out.println("Error: invalid input command");
								}
							} else {
								// Else there is no token, give error
								System.out.println("Error: invalid input command");
							}
						}
					} else {
						// Else the coordinate is not valid, give error
						System.out.println("Error: invalid coordinate expression");
					}
				} else {
					// Else there is no token, give error
					System.out.println("Error: invalid input command");
				}
			} else

			{
				// Else retrieve and print value of cell
				String printVal = g.getFromGrid(coord, 'p');
				if (printVal.contains("Error")) {
					System.out.println(printVal);
				} else {
					System.out.println(coord + " = " + printVal);
				}
			}
		} else {
			// Else there are no tokens, give error
			System.out.println("Error: blank input");
		}

		scanInput.close();
	}

	// Formula Command: processing SUM/AVG/SORT commands
	public static void calcFormCmd(String coord, String formula, boolean sort) {
		// Using Scanner to Store Parts of Command
		Scanner scanForm = new Scanner(formula);
		boolean validFI = true;
		char range = ' ';
		String coord1 = "", dash = "", coord2 = "", cmdType = scanForm.next();

		// Lots of error checking
		if (scanForm.hasNext()) {
			coord1 = scanForm.next();
		} else {
			validFI = false;
		}
		if (scanForm.hasNext()) {
			dash = scanForm.next();
		} else {
			validFI = false;
		}
		if (scanForm.hasNext()) {
			coord2 = scanForm.next();
		} else {
			validFI = false;
		}

		if (validFI && dash.equals("-") && Cell.validCoord(coord1) && Cell.validCoord(coord2)) {
			Cell c1 = new Cell(null, coord1), c2 = new Cell(null, coord2);

			boolean c2Bound = c1.row <= 9 && c1.row >= 0 && c1.column <= 6 && c1.column >= 0;
			if (c1.row <= 9 && c1.row >= 0 && c1.column <= 6 && c1.column >= 0 && c2Bound) {

				// Set Cell Range as Horizontal, Vertical, or Invalid
				if (c1.row == c2.row) {
					if (c1.column >= c2.column) {
						range = 'i';
					} else {
						range = 'h';
					}
				} else if (c1.column == c2.column) {
					if (c1.row >= c2.row) {
						range = 'i';
					} else {
						range = 'v';
					}
				} else {
					range = 'i';
				}

				// Either Sending Command to AVG/SUM method or SORT Method
				if (sort) {
					processSort(cmdType.equalsIgnoreCase("SORTA"), range, coord1, coord2);
				} else {
					processSumAvg(cmdType.equalsIgnoreCase("AVG"), range, coord, coord1, coord2, formula);
				}

			} else {
				System.out.println("Error: one or more cells out of bounds");
			}
			scanForm.close();
		} else {
			System.out.println("Error: invalid formula input");
		}

	}

	// Process SUM/AVG: Processing and Calculating a Sum or Average Cell
	public static void processSumAvg(boolean isAVG, char range, String crd, String crd1, String crd2, String formula) {
		Cell c1 = new Cell(null, crd1), c2 = new Cell(null, crd2);
		String cellForm = (isAVG) ? "( " : "";

		// Using Fencepost Loop to Add Cell Values to FormulaCell Expression
		if (range == 'h') {
			for (int i = c1.column; i < c2.column; i++) {
				cellForm += g.getFromGrid((char) (i + 65) + "" + (c1.row + 1), 'v') + " + ";
			}
			cellForm += g.getFromGrid(crd2, 'v');

			// If average calculation, divide by number of cells
			if (isAVG) {
				cellForm += " ) / " + (c2.column - c1.column + 1);
			}

			// Creating FormulaCell or BigFormulaCell w/ Calculations
			FormulaCell fc = new FormulaCell(cellForm, crd, true, formula);
			if (fc.BFCVals[0] == null) {
				g.addToGrid(fc);
			} else {
				g.addToGrid(new BigFormulaCell(fc.BFCVals[0], fc.BFCVals[1], fc.BFCVals[2], fc.hasCoord));
			}

		} else if (range == 'v') {
			// Very similar to above if statement but for vertical ranges
			for (int i = c1.row; i < c2.row; i++) {
				cellForm += g.getFromGrid((char) (c1.column + 65) + "" + (i + 1), 'v') + " + ";
			}
			cellForm += g.getFromGrid(crd2, 'v');

			if (isAVG) {
				cellForm += " ) / " + (c2.row - c1.row + 1);
			}

			FormulaCell fc = new FormulaCell(cellForm, crd, true, formula);
			if (fc.BFCVals[0] == null) {
				g.addToGrid(fc);
			} else {
				g.addToGrid(new BigFormulaCell(fc.BFCVals[0], fc.BFCVals[1], fc.BFCVals[2], fc.hasCoord));
			}

		} else {
			// Else range == 'i', cell range is invalid
			System.out.println("Error: invalid cell range");
		}
	}

	// Process Sort: Processing and Sorting Cells Ascending or Descending
	public static void processSort(boolean isSA, char range, String crd1, String crd2) {
		Cell c1 = new Cell(null, crd1), c2 = new Cell(null, crd2);
		Cell types = new Cell(null, crd1);

		// Getting Cell Type of C1
		Cell c = g.spreadsheet[c1.row][c1.column];
		if (c instanceof FormulaCell) {
			types = (FormulaCell) c;
		} else if (c instanceof DateCell) {
			types = (DateCell) c;
		} else if (c instanceof TextCell) {
			types = (TextCell) c;
		}

		// 
		boolean sameType = true, nullVal = false;
		if (range == 'h' || range == 'v') {
			Cell current;
			int cellRange = c2.column - c1.column + 1;

			if (range == 'v') {
				cellRange = c2.row - c1.row + 1;
			}

			Cell[] sortArr = new Cell[cellRange];
			for (int i = 0; i <= cellRange - 1; i++) {
				if (range == 'h') {
					current = g.spreadsheet[c1.row][c1.column + i];
				} else {
					current = g.spreadsheet[c1.row + i][c1.column];
				}
				if (current == null) {
					System.out.println("Error: reference to invalid cell");
					nullVal = true;
					// Break, otherwise code will throw NullPointerException
					break;
				}
				boolean bigForm = current.getClass().isAssignableFrom(types.getClass())
						|| types.getClass().isAssignableFrom(current.getClass());

				if (!bigForm && !current.getClass().equals(types.getClass())) {
					sameType = false;
				}
				sortArr[i] = current;
			}

			if (sameType) {
				if (!nullVal) {
					Arrays.sort(sortArr);
					for (int i = 0; i <= cellRange - 1; i++) {
						if (range == 'h') {
							if (isSA) {
								g.spreadsheet[c1.row][c1.column + i] = sortArr[i];
							} else {
								g.spreadsheet[c1.row][c1.column + cellRange - 1 - i] = sortArr[i];
							}
						} else {
							if (isSA) {
								g.spreadsheet[c1.row + i][c1.column] = sortArr[i];
							} else {
								g.spreadsheet[c1.row + cellRange - 1 - i][c1.column] = sortArr[i];
							}
						}
					}
				}
			} else {
				System.out.println("Error: multiple cell types in range");
			}

		} else {
			System.out.println("Error: invalid cell range");
		}
	}
}