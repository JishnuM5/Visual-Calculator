
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Arrays;

public class DecCalc {

	// This method processes the input to be calculated
	public static String processInput(String input, boolean printStep) {
		// Creating scanner, array length vars, step printing var, final ans, and a flag
		Scanner scan = new Scanner(input);
		int wordNum = 1;
		String finalAns = "";
		boolean printSteps = printStep;
		boolean doubleSpace = false;
		// If the length of the input is 0, return a blank input error
		if (input.length() == 0) {
			scan.close();
			return "Error: blank input";
		}

		// For loop traversing input as a char array, counting # of tokens
		for (char c : input.toCharArray()) {
			if (c == ' ' && !doubleSpace) {
				// If char is a space and the previous char wasn't, +1 to array length
				wordNum++;
				doubleSpace = true;
			} else if (!(c == ' ')) {
				// If char is not a space, no double space
				doubleSpace = false;
			}
		}
		// Declaring array to store input, +1 to store extra space (used later on)
		String[] inputVal = new String[wordNum + 1];
		for (int i = 0; i < wordNum; i++) {
			inputVal[i] = scan.next();
		}
		inputVal[inputVal.length - 1] = " ";

		// Checking for bounds errors or calling method to find answer and returning it
		String o = inputVal[0];
		boolean tk1 = o.equals("+") || o.equals("-") || o.equals("*") || o.equals("/") || o.equals("^");
		// Checking if input is long enough, first token isn't an operator
		if (wordNum > 2 && !tk1) {
			if (printSteps) {
				System.out.println("Calculation:");
			}
			finalAns = orderOfOp(inputVal, printSteps);
		} else {
			// Triggers invalid expression error
			inputVal[inputVal.length - 1] = "";
		}
		// If there is already an error, then no change --> !contains("Error")
		if (inputVal[inputVal.length - 1].equals(" ") && !finalAns.contains("Error")) {
			// If inputVal array hasn't changed (extra space still at end), error
			finalAns = "Error: no valid operator found";
		} else if (!inputVal[1].equals(" ") && !finalAns.contains("Error")) {
			// If calculations didn't complete (extra space isn't second element), error
			finalAns = "Error: invalid expression";
			if (inputVal[0].contains("inside parentheses")) {
				// If error inside parentheses, add to error message
				finalAns += " inside parentheses";
			}
		}
		scan.close();
		return finalAns;
	}

	// This method sends parts to be calculated based on Order of Operations
	public static String orderOfOp(String[] inputVal, boolean printSteps) {
		// Various flags declared
		boolean hasOpr = true, parenPair = true, paren, pow, mulDiv;

		// Loop that runs while there isn't error and there is an operator
		while (!inputVal[0].contains("Error") && hasOpr) {
			int i = 0, parenNum = 0;
			hasOpr = pow = paren = mulDiv = false;
			for (String sym : inputVal) {
				// Go through array once to check operators and parentheses
				if (sym.equals("(")) {
					hasOpr = paren = pow = mulDiv = true;
					parenPair = !parenPair;
				} else if (sym.equals("^")) {
					hasOpr = pow = mulDiv = true;
				} else if (sym.equals("*") || sym.equals("/")) {
					hasOpr = mulDiv = true;
				} else if (sym.equals(")")) {
					// Every time parentheses found, flag switches, # of parentheses +1
					parenPair = !parenPair;
					parenNum++;
				}
			}

			if (!parenPair) {
				// If all parentheses aren't paired, error and for loop won't run
				inputVal[0] = "Error: missing parenthesis";
				i = inputVal.length;
			}

			if (Arrays.toString(inputVal).contains("  ") && printSteps) {
				// If not a calculation inside parentheses (no extra space at end), print array
				for (String s : inputVal) {
					// Cleaner look than Arrays.toString()
					System.out.print(s + " ");
				}
				System.out.println();
			}

			for (i = i; i < inputVal.length; i++) {
				// Go through array a second time to call calculate
				if (inputVal[i].equals("(")) {
					// More declaration: move array buy how much, closed parentheses
					int moveArr = 0, closedParen = inputVal.length;
					String prev = "";

					// Searching for innermost parentheses, while parentheses still there
					while (parenNum > 0) {
						closedParen--;
						if (inputVal[closedParen].equals(")")) {
							// if parentheses found, # of paren -1
							parenNum--;
						}
					}
					// Checking if any opening parentheses up to point of closed one
					for (int j = i; j < closedParen; j++) {
						if (inputVal[j].equals("(")) {
							// If found, i set to index of open parentheses
							i = j;
						}
					}
					if (closedParen - i < 4) {
						// If there are less than 3 elements between parentheses, error
						return "Error: invalid input inside parentheses";
					}
					moveArr = closedParen - i;

					// Calculations inside parentheses done as a another 'input'
					prev = orderOfOp(Arrays.copyOfRange(inputVal, i + 1, closedParen), printSteps);
					if (prev.contains(" ") && !prev.contains("Error")) {
						// If no error and calculations completed, answer put into original array
						prev = prev.substring(0, prev.length() - 1);
						inputVal = orderOfOp2(inputVal, i, moveArr, prev);
					} else {
						// Else, error sent back
						return prev + " inside parentheses";
					}
					i = inputVal.length;
				} else if (inputVal[i].equals("^") && !paren) {
					inputVal = orderOfOp2(inputVal, i - 1, 2, "none");
				} else if ((inputVal[i].equals("*") || inputVal[i].equals("/")) && !pow) {
					inputVal = orderOfOp2(inputVal, i - 1, 2, "none");
				} else if ((inputVal[i].equals("+") || inputVal[i].equals("-")) && !mulDiv) {
					inputVal = orderOfOp2(inputVal, i - 1, 2, "none");
					hasOpr = true;
				}
			}
		}

		if (inputVal[0].contains("Error")) {
			// If there is error, just send that back
			return inputVal[0];
		}
		return inputVal[0] + inputVal[1];
		// Otherwise, send it along with 2nd element, to check for errors in prev
		// Doesn't affect because it just adds an extra space in final answer
	}

	// This method processes a single operation. calculates it, and deals w/ errors
	public static String[] orderOfOp2(String[] inputVal, int i, int moveArr, String prev) {

		// Declaration: arrays to store errors and/or fraction values
		BigDecimal bigNum1 = BigDecimal.ZERO, bigNum2 = BigDecimal.ZERO;
		double num1 = 0, num2 = 0;
		// Boolean and error arrays used so they pass by reference, same across methods
		boolean[] bigDec = new boolean[1];
		String error[] = { "" };
		String ans = "", opr = "";

		if (prev.equals("none")) {
			// If there is no previous answer, calculate
			opr = inputVal[i + 1];
			if (!(checkNum(inputVal[i], bigDec).equals("n/a") || checkNum(inputVal[i + 2], bigDec).equals("n/a"))) {
				// If the numbers are valid, don't send an error message

				// Creates vars to store current calc values, either as BigDecimal or double
				if (bigDec[0]) {
					bigNum1 = new BigDecimal(inputVal[i]);
					bigNum2 = new BigDecimal(inputVal[i + 2]);
				} else {
					num1 = Double.parseDouble(inputVal[i]);
					num2 = Double.parseDouble(inputVal[i + 2]);
				}

			} else {
				error[0] = "Error: invalid expression";
				return error;
			}

			// Updates error or bigDec array
			errorChecker(num1, num2, bigNum1, bigNum2, opr, bigDec, error);

			if (!error[0].contains("Error")) {
				if (bigDec[0]) {
					bigNum1 = new BigDecimal(inputVal[i]);
					bigNum2 = new BigDecimal(inputVal[i + 2]);
					errorChecker(num1, num2, bigNum1, bigNum2, opr, bigDec, error);
					if (!error[0].contains("Error")) {
						ans = calc(bigNum1, bigNum2, opr);
					} else {
						return error;
					}
				} else {
					ans = calc(num1, num2, opr);

				}
			} else {
				return error;
			}
		} else {
			// If a previous answer is being inserted, then no calculations
			ans = prev;
			if (!ans.contains("Error")) {
				checkNum(prev, bigDec);

			}
		}
		// Replacing old expressions with answer, shifting rest of array
		for (int j = i + 1; j < inputVal.length - moveArr; j++) {
			// All up to moveArr shifted to left as original values
			inputVal[j] = inputVal[j + moveArr];
		}
		for (int j = inputVal.length - moveArr; j < inputVal.length; j++) {
			// Non-breaking space used to fill rest of array, ASCII 255
			inputVal[j] = " ";
		}
		inputVal[i] = ans;

		return inputVal;
	}

	// This method checks whether inputs are valid numbers
	public static String checkNum(String num, boolean[] bigDec) {
		boolean isNum = true;
		Scanner scan = new Scanner(num);

		if (scan.hasNextDouble()) {
			// If not a double, check if Big Decimal, or number is invalid
			String scanVal = scan.next();
			double value = Double.parseDouble(scanVal);
			if ((value + "").equals("Infinity")) {
				bigDec[0] = true;
			}
		} else {
			isNum = false;
		}
		scan.close();

		// Returning values based on number validity
		if (isNum) {
			return num;
		} else {
			bigDec[0] = false;
			return "n/a";
		}
	}

	// This method does the actual calculations
	public static String calc(double num1, double num2, String opr) {
		
		// Perform operations based on operator sign
		String ans = "";
		if (opr.equals("+")) {
			ans = num1 + num2 + "";
		} else if (opr.equals("-")) {
			ans = num1 - num2 + "";
		} else if (opr.equals("*")) {
			ans = num1 * num2 + "";
		} else if (opr.equals("/")) {
			ans = num1 / num2 + "";
		} else if (opr.equals("^")) {
			ans = Math.pow(num1, num2) + "";
		}

		return ans;
	}

	// This method does the actual calculations with Big Decimals and its methods
	public static String calc(BigDecimal bigNum1, BigDecimal bigNum2, String opr) {
		// Perform operations based on operator sign
		String ans = "";
		if (opr.equals("+")) {
			ans = bigNum1.add(bigNum2) + "";
		} else if (opr.equals("-")) {
			ans = bigNum1.subtract(bigNum2) + "";
		} else if (opr.equals("*")) {
			ans = bigNum1.multiply(bigNum2) + "";
		} else if (opr.equals("/")) {
			ans = bigNum1.divide(bigNum2) + "";
		} else if (opr.equals("^")) {
			ans = bigNum1.pow(bigNum2.intValue()) + "";
		}
		return ans;
	}

	// This method checks whether or not a calculation will require Big Decimals
	public static boolean calcIsBigDec(double num1, double num2, String opr) {
		double ans = Double.parseDouble(calc(num1, num2, opr));

		// Performs opposite of each operation
		// Checking if calculations provide same answer both ways (if not -> BigDec)
		if (opr.equals("+")) {
			return !(ans - num2 == num1);
		} else if (opr.equals("-")) {
			return !(ans + num2 == num1);
		} else if (opr.equals("*")) {
			return !(ans / num2 == num1);
		} else if (opr.equals("/")) {
			return !(ans * num2 == num1);
		} else {
			return true;
		}

	}

	// This method checks for some possible errors
	public static void errorChecker(double num1, double num2, BigDecimal bigNum1, BigDecimal bigNum2, String opr,
			boolean[] bigDec, String[] error) {
		if (bigDec[0]) {
			boolean numr2Is0 = bigNum2.compareTo(BigDecimal.ZERO) == 0;
			if (opr.equals("/") && numr2Is0) {
				// If 2nd num is 0 and opr is /, gives error
				error[0] = "Error: divide by 0";
			} else if (opr.equals("^")) {
				int intEx = bigNum2.compareTo(BigDecimal.valueOf(bigNum2.intValue()));
				int positiveEx = bigNum2.compareTo(BigDecimal.ZERO);
				if (intEx != 0 || positiveEx <= 0) {
					// If exponent is not a positive int, gives error
					error[0] = "Error: invalid exponent";
				}
			}
		} else {
			if (opr.equals("/") && num2 == 0) {
				// If either denominator = 0 or dividing and and num2 = zero, error
				error[0] = "Error: divide by 0";
			} else if (calcIsBigDec(num1, num2, opr)) {
				// If a calculation will involve Big Decimals, update that value
				bigDec[0] = true;
			}
		}

	}
}