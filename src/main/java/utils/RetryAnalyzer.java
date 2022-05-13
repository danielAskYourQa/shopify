package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class RetryAnalyzer extends TestListenerAdapter implements IRetryAnalyzer {

  private static final SimpleDateFormat TEST_RUN_DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

  private static final int maxTry = 2;
  private int count = 0;

  @Override
  public boolean retry(ITestResult test) {
    //Check if test not succeed
    if (!test.isSuccess()) {
      //Check if max try count is reached
      if (count < maxTry) {
        //Increase the maxTry count by 1
        count++;
        //Mark test as failed
        test.setStatus(ITestResult.FAILURE);
        getTestResult(test);
        printOnTestFailureDetails(test);
        //Tells TestNG to re-run the test
        return true;
      } else {
        //If maxCount reached,test marked as failed
        test.setStatus(ITestResult.FAILURE);
        getTestResult(test);
        onTestFailure(test);
      }
    } else {
      //If test passes, TestNG marks it as passed
      test.setStatus(ITestResult.SUCCESS);
      getTestResult(test);
      onTestSuccess(test);
    }
    return false;
  }

  @Override
  public void onTestFailure(ITestResult test) {
    if (!test.isSuccess()) {
      System.out.println("This test [" + getFullTestPath(test)
          + " is marked as failed! \n------------------------------------------------------------------------------------------------------------");
    }
  }

  @Override
  public void onTestSuccess(ITestResult tr) {
    printTestDetails(tr);
    if (tr.isSuccess()) {
      System.out.println("The test [" + getFullTestPath(tr) + "]" + " is marked as passed!");
      System.out.println(
          "------------------------------------------------------------------------------------------------------------");
    }
  }

  private void printOnTestFailureDetails(ITestResult tr) {
    String failMessage = tr.getThrowable().getMessage();
    System.out.println(
        "------------------------------------------------------------------------------------------------------------");
    System.out.println("The test [" + getFullTestPath(tr) + "] has failed!");
    if (failMessage != null) {
      System.out.println("Failure message: " + failMessage);
      if (tr.getThrowable().getStackTrace() != null) {
        StackTraceElement[] stackTraceElements = tr.getThrowable().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
          System.out.println("\tat " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName()
              + "(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")");
        }
      }
    }
    System.out.println(
        "------------------------------------------------------------------------------------------------------------");
  }

  private String getFullTestPath(ITestResult tr) {
    return tr.getTestClass().getRealClass().getSimpleName() + "," + tr.getMethod().getMethodName();
  }

  private void printTestDetails(ITestResult testResult) {
    System.out.println(
        "------------------------------------------------------------------------------------------------------------");
    System.out.println("Test -> " + testResult.getName() + " (" + getFormattedCurrentTime() + ") ");
    System.out.println(getTestResult(testResult));
    System.out.println(
        "------------------------------------------------------------------------------------------------------------");
  }

  private String getTestResult(ITestResult tr) {
    String tResult = "Test Result = ";
    if (tr.isSuccess()) {
      return tResult + "SUCCESS!!!";
    } else {
      return tResult + "FAILURE!!!";
    }
  }

  private String getFormattedCurrentTime() {
    return TEST_RUN_DATE_FORMATTER.format(new Date());
  }
}
