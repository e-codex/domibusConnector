/*
 * 
 */
package org.holodeck.backend.module.exception;

/**
 * The Class DownloadMessageFault.
 */
public class DownloadMessageFault extends java.lang.Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -68841730797017753L;

	/** The fault message. */
	private backend.ecodex.org.FaultDetail faultMessage;

	/**
	 * Instantiates a new download message fault.
	 */
	public DownloadMessageFault() {
		super("DownloadMessageFault");
	}

	/**
	 * Instantiates a new download message fault.
	 *
	 * @param s the s
	 */
	public DownloadMessageFault(java.lang.String s) {
		super(s);
	}

	/**
	 * Instantiates a new download message fault.
	 *
	 * @param s the s
	 * @param ex the ex
	 */
	public DownloadMessageFault(java.lang.String s, java.lang.Throwable ex) {
		super(s, ex);
	}

	/**
	 * Instantiates a new download message fault.
	 *
	 * @param cause the cause
	 */
	public DownloadMessageFault(java.lang.Throwable cause) {
		super(cause);
	}

	/**
	 * Sets the fault message.
	 *
	 * @param msg the new fault message
	 */
	public void setFaultMessage(backend.ecodex.org.FaultDetail msg) {
		faultMessage = msg;
	}

	/**
	 * Gets the fault message.
	 *
	 * @return the fault message
	 */
	public backend.ecodex.org.FaultDetail getFaultMessage() {
		return faultMessage;
	}
}
