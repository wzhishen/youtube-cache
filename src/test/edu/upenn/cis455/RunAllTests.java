package test.edu.upenn.cis455;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit test suite.
 * @author Zhishen Wen
 * @version Apr, 2013
 */
public class RunAllTests extends TestCase 
{
  public static Test suite() 
  {
    try {
      Class[]  testClasses = {
        /* Names of unit test classes */
        Class.forName("test.edu.upenn.cis455.CacheSystemTest_DAO"),
        Class.forName("test.edu.upenn.cis455.CacheSystemTest_Item"),
        Class.forName("test.edu.upenn.cis455.ServiceSystemTest_Client"),
        Class.forName("test.edu.upenn.cis455.ServiceSystemTest_NodeMessage"),
        Class.forName("test.edu.upenn.cis455.ServiceSystemTest_UI")
      };   
      
      return new TestSuite(testClasses);
      
    } catch(Exception e) {
      e.printStackTrace();
    } 
    return null;
  }
  
}
