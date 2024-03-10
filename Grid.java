
public class Grid {
	Cell[][] spreadsheet = new Cell[10][7];

	// Printing Grid: prints grid on command
	public void printGrid() {
		// Column Headers
		System.out.print("       |");
		// Nested For Loop: traverses spreadsheet and prints box
		for (int i = 'A'; i <= 'G'; i++) {
			System.out.printf("%4c   |", (char) i);
		}
		System.out.println("\n––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––");

		// Traverse Grid, Row Headers and Boxes
		for (int r = 0; r < 10; r++) {
			System.out.printf("%4d   |", r + 1);
			for (int c = 0; c < 7; c++) {
				if (spreadsheet[r][c] == null) {
					// If the element is empty, print empty cell
					System.out.print("       |");
				} else {
					if (spreadsheet[r][c].printVal == null) {
						System.out.print("       |");
					} else {
						// Else print cell with formatted value
						String pV = spreadsheet[r][c].printVal;

						if (spreadsheet[r][c] instanceof FormulaCell && !pV.contains("error")) {
							// If the cell is a FormulaCell, get the value;
							pV = getFromGrid((char) (c + 65) + "" + (r + 1), 'v');
						}
						if (pV.length() > 7) {
							pV = pV.substring(0, 7);
						}
						System.out.printf("%-7s|", pV);
					}
				}
			}
			System.out.println("\n––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––");
		}
	}

	// Cell Added to Grid: checks if cell is within grid and adds to array
	public void addToGrid(Cell addCell) {
		int row = addCell.row, col = addCell.column;

		if (row <= 9 && row >= 0 && col <= 6 && col >= 0) {
			// If cell row index is [0, 9], column index is [0, 6], add cell
			spreadsheet[row][col] = addCell;
			String pV = addCell.printVal;

			if (addCell instanceof FormulaCell) {
				FormulaCell fc = (FormulaCell) addCell;
				if (fc.sumAvgForm != null) {
					pV = fc.sumAvgForm;
				}
			}

			System.out.printf("%s has been mapped to (%d, %d) \n", pV, col, row);
		} else {
			System.out.println("Error: one or more cells out of bounds");
		}
	}

	// Get Value from Grid: breaks down coord to retrieve a value of a cell
	public String getFromGrid(String coord, char valType) {
		if (Cell.validCoord(coord)) {
			// if coordinates of the cell are valid, continue
			int col = (int) coord.charAt(0) - 65;
			int row = Integer.parseInt(coord.substring(1)) - 1;

			if (row <= 9 && row >= 0 && col <= 6 && col >= 0) {
				if (spreadsheet[row][col] != null) {
					// If the cell is not null, return cell's value depending on wanted type
					if (valType == 'p') {
						if (spreadsheet[row][col] instanceof FormulaCell) {
							FormulaCell fc = (FormulaCell) spreadsheet[row][col];
							if (fc.sumAvgForm != null) {
								return fc.sumAvgForm;
							}
						}
						return spreadsheet[row][col].printVal;
					} else {
						// Explicit Downcasting to FormulaCell
						Cell cell = spreadsheet[row][col];
						if (cell instanceof FormulaCell && cell != null) {
							FormulaCell fc = (FormulaCell) cell;
							if (fc.hasError) {
								// If the cell has an error, return placeholder
								return "X";
							} else {
								if (fc instanceof BigFormulaCell) {
									// If FormulaCell is a BigFormulaCell, retyrn BigDecimal value
									BigFormulaCell bfc = (BigFormulaCell) fc;
									return bfc.bigValue + "";
								} else {
									return fc.value + "";
								}
							}
						} else {
							return "X";
						}
					}
				} else {
					return "null";
				}
			} else {
				return "Error: one or more cells out of bounds";
			}
		}
		return "Error: invalid coordinate expression";
	}

	// TODO: print message to inform cell is cleared instead of null mapped
	// Clear: this method clears either the whole spreadsheet or just a cell
	public void clear(String input) {
		if (input.length() > 5) {
			// If length > 5, then clearing a cell
			if (Cell.validCoord(input.substring(6))) {
				addToGrid(new Cell(null, input.substring(6)));
			} else {
				System.out.println("Error: invalid coordinate expression");
			}
		} else {
			spreadsheet = new Cell[10][7];
		}
	}

	// Returned Boolean tells if Call is Invalid (Error)
	public boolean updateValues(int row, int col) {
		if (row == -1) {
			// If row is -1, all cells updated
			for (Cell[] arr : spreadsheet) {
				for (Cell elem : arr) {
					if (elem instanceof FormulaCell && elem != null && !elem.printVal.contains("error")) {
						FormulaCell fc = (FormulaCell) spreadsheet[elem.row][elem.column];
						String coord = (char) (elem.column + 65) + "" + (elem.row + 1);

						if (fc.hasCoord) {
							// If element is a valid FormulaCell with coordinates, update
							FormulaCell upd = new FormulaCell(fc.printVal, coord, false);

							// If element is a BigFormulaCell, update as such
							if (fc instanceof BigFormulaCell) {
								BigFormulaCell bfc = (BigFormulaCell) fc;
								bfc.bigValue = new BigFormulaCell(upd.BFCVals[0], upd.BFCVals[1], upd.BFCVals[2],
										true).bigValue;
							} else {
								fc.value = upd.value;
							}
						}
					}
				}
			}
		} else {
			// Else, just update specific cell
			if (row <= 9 && row >= 0 && col <= 6 && col >= 0) {
				Cell cell = spreadsheet[row][col];
				if (cell instanceof FormulaCell && cell != null && !cell.printVal.contains("error")) {
					FormulaCell fc = (FormulaCell) cell;
					String coord = (char) (col + 65) + "" + (row + 1);
					
					if (fc.hasCoord) {
						// If element is a valid FormulaCell with coordinates, update
						FormulaCell upd = new FormulaCell(fc.printVal, coord, false);
						
						// If element is a BigFormulaCell, update as such
						if (fc instanceof BigFormulaCell) {
							BigFormulaCell bfc = (BigFormulaCell) fc;
							bfc.bigValue = new BigFormulaCell(upd.BFCVals[0], upd.BFCVals[1], upd.BFCVals[2],
									true).bigValue;
						} else {
							fc.value = upd.value;
						}
					}
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}
}