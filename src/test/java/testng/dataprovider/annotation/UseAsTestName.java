package testng.dataprovider.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation used as an indicator that the Test Method should use the indexed
 * parameter as the test instance name
 *
 * @author jmochel
 */
 
@Retention(RetentionPolicy.RUNTIME)
public @interface UseAsTestName {
 
 /**
  * Index of the parameter to use as the Test Case ID.
  */
 
 int idx() default 0;
 
}