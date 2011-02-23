package com.plugtree.solrmeter.model.exception;

/**
 * Excetion that indicate that the server doesn't have the ping command configured
 * @author tflobbe
 *
 */
public class PingNotConfiguredException extends OperationException {
	
	private static final long serialVersionUID = -1223617109567994001L;

	public PingNotConfiguredException(String message) {
		super(message);
	}

	@Override
	public String getOperationName() {
		return "ping";
	}

}
