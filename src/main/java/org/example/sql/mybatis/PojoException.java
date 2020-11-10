package org.example.sql.mybatis;

/**
 * @author bonult
 * @description
 * @version v1.0
 */
public class PojoException extends RuntimeException {

	private static final long serialVersionUID = -1861199425575765832L;

	public PojoException(String msg) {
		super(msg);
	}

	public PojoException(String msg, Throwable e) {
		super(msg, e);
	}
}
