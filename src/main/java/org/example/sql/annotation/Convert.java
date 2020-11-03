package org.example.sql.annotation;


/**
 * @author bonult
 * @version v1.0
 */
//@FunctionalInterface
public interface Convert {

    String transform(Object field, String convert, String note);

}
