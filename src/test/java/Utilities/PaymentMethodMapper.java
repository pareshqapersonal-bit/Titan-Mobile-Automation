package Utilities;

public class PaymentMethodMapper {
	
	public enum PaymentMethod {

	    GOOGLE_PAY("Google Pay"),
	    PHONEPE("Phonepe"),
	    PAYTM("Paytm"),
	    OTHER_UPI("Other UPI"),
	    NET_BANKING("Net Banking"),
		CREDIT_DEBIT_CARD("Credit / Debit Card"),
		CASH_ON_DELIVERY("Cash on Delivery"),
		WALLET("Wallet");

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
