
import java.math.BigDecimal;

// BigFormulaCell: a class that stores FormulaCells with BigDecimals
public class BigFormulaCell extends FormulaCell {
	// Value field for BigDecimal
	public BigDecimal bigValue;

	//Number Constructor
	public BigFormulaCell(String bigValue, String coord) {
		super(bigValue, coord);
		this.bigValue = new BigDecimal(bigValue.trim());
	}

	//Formula Constructor
	public BigFormulaCell(String formula, String bigValue, String coord, boolean hasCoord) {
		super(bigValue, coord);
		this.bigValue = new BigDecimal(bigValue.trim());
		printVal = formula;
		this.hasCoord = hasCoord;
	}

}
