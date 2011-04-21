I. The structure:

  1. directory 'testsXML' - place here the XML description files used 
     as test cases. Don't forget to commit these file to SVN.
     
  2. directory 'log' - the correct log for each simulation. The log name 
     is exacly the name of the simulation file (including extension).
     Don't forget to commint this file to SVN.
     
  3. directory 'testsJava' - nothing to do here by the human user.
     Please don't commit this directory before calling <ant clear>.
  
II. ANT file:

  1. For being sure that you have the last AORS version, you have to call: 
  
       ant build 
       
     from within the directory 'testCases'. Don't need to care about where 
     and how is build the aors distribution. 
     This task is REQUIRED before you can run any test cases!
     
  2. For each test case will be defined a target. To execute a test case 
     we will have to call:
     
       ant testCaseName
       
     from within the directory 'testCases'. This will load the XML file,
     generate the code, compile the code and run the simulation. Finally
     it will call a diff tool for analize the logs.
     
  3. After performing the test you want, please call:
  
       ant clear
       
     from within the directory 'testCases'. This task will perform the cleaning 
     of all generated stuffs, so you don't need to worry about commiting the
     directory with a lot of not wanted code. 
     Please perform this task after you have done all tests, so you don't 
     commit a lot of unwanted code that was generated for tests!
     
  4. Usage example:
  
      ant build    (REQUIRED before starting tests!)  
      
      
      ant test     (No diference between logs)
      
      ----------- Result ---------------------------------------
      D:\SVN\AORS-Trunk\projects\testCases>ant test
      Buildfile: build.xml

      test:
       [java] __________________________________________________________________
       [java]
       [java] Directory in use: 'D:\SVN\AORS-Trunk\projects\testCases\testsXML\'
       [java] Start performing test for file: 'test.xml'
       [java] This may take some time, please be patient.
       [java]
       [java] Can not load the file properties.xml. Using the default values.
       [java] Performing cleaning...please wait...OK
       [java] Loading XML simulation description...please wait...OK
       [java] Validating simulation description...please wait...OK
       [java] Generating Java code...please wait...OK
       [java] Compiling the generated Java code...please wait...OK
       [java] Running the simulation...please wait...OK
       [java] Task finished for file: D:\SVN\AORS-Trunk\projects\testCases\testsXML\test.xml
  
      BUILD SUCCESSFUL
      Total time: 10 seconds
      ----------- END Result ---------------------------------------
      
      
      
      ant test1    (There are diference between logs)
      
      ----------- Result ---------------------------------------
      D:\SVN\AORS-Trunk\projects\testCases>ant test1
      Buildfile: build.xml

      test1:
       [java] __________________________________________________________________
       [java]
       [java] Directory in use: 'D:\SVN\AORS-Trunk\projects\testCases\testsXML\'
       [java] Start performing test for file: 'test1.xml'
       [java] This may take some time, please be patient.
       [java]
       [java] Can not load the file properties.xml. Using the default values.
       [java] Performing cleaning...please wait...OK
       [java] Loading XML simulation description...please wait...OK
       [java] Validating simulation description...please wait...OK
       [java] Generating Java code...please wait...OK
       [java] Compiling the generated Java code...please wait...OK
       [java] Running the simulation...please wait...OK
       [java] Task finished for file: D:\SVN\AORS-Trunk\projects\testCases\testsXML\test1.xml
       
       [exec] Files testsJava/test1/log/test1.xml and logs/test1.xml differ
       [exec] Result: 1
  
      BUILD SUCCESSFUL
      Total time: 6 seconds
      ----------- END Result ---------------------------------------
      
             
      ant clear   (REQUIRED - not after each test, but when you finish testing!)
 
III Test cases:

  Please complete here the names of the tasks you add in the build file, so 
  anyone can know which tests are available and what is the feature that is
  tested. Some tasks may contain mutiple tests, this meaning that we test 
  mutiple features at once.
  
  When write a XML description test file, please keep in mind to:
  - use no step delay time 
      e.g. <SimulationParameters ... stepTimeDelay="0" .../> 
    so the simulation may run as fast as possible;
  - use a fixed random seed, to the logs can be compared;
      e.g. <SimulationParameters ... randomSeed="5" .../>
  - use a number of steps that help to have a good test, but no more than 
    necessarily, this way the test will not take more time than is needed.
    
  SInce it is also possible to use a GUI DIff Application, we have to create 
  aditional tasks named  "testName-diff" for each projects. This will be called 
  only if needed after we see that we have some differences between logs.
  
  Test task names:
  
  1. <test> - this is just a test for the testing tool... 
              No difference between logs.
     <test-diff> - start diff gui tool to compare these logs.
              
  2. <test1> - this is just a test for the testing tool... 
               There are difference between logs.
     <test1-diff> - start diff gui tool to compare these logs.
               
  3. <...> - comming real test cases....
     