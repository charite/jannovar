/**
 * 
 */
package jannovar.exception;

import java.io.IOException;

/**
 * {@link InvalidAttributException} are thrown if a attribute given to a method is invalid and can not be processed. 
 * @author mjaeger
 *
 */
public class InvalidAttributException extends IOException {

	public InvalidAttributException(String msg) {
		super(msg);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
