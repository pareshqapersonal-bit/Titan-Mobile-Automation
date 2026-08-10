package Utilities;

public class PaymentMethodMapper {
	
	public enum PaymentMethod {

	    GOOGLE_PAY("Google Pay"),
	    PHONEPE("Phonepe"),
	    PAYTM("Paytm"),
	    OTHER_UPI("Other UPI"),
	    NET_BANKING("Net Banking");

	    private final String excelValue;

	    PaymentMethod(String excelValue) {
	        this.excelValue = excelValue;
	    }

	    public String getExcelValue() {
	        return excelValue;
	    }

	    public static PaymentMethod fromExcel(String value) {

	        for (PaymentMethod method : values()) {

	            if (method.excelValue.equalsIgnoreCase(value.trim())) {
	                return method;
	            }
	        }

	        throw new IllegalArgumentException(
	            "Unsupported payment method: " + value
	        );
	    }
	}

}
