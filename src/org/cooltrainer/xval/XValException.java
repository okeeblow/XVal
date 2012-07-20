package org.cooltrainer.xval;

public class XValException extends Exception {

	private static final long serialVersionUID = 1L;

	public XValException() {

	}

	public XValException(String detailMessage) {
		super(detailMessage);
	}

	public XValException(Throwable throwable) {
		super(throwable);
	}

	public XValException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
