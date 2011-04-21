*****************************************************************************
* HowTo use the main-build-file to create a AOR-Java-Simulator distribution *
*****************************************************************************

The build.xml in the folder creates a distribution with following structure:

     /ext/aorsl           - the AORSL-Schema files
     /ext/doc             - some additional information
     /ext/javagen         - the XSLT for code creation
     /media/images        - images, pics, etc.
     /media/sounds        - mp3-sounds
     /lib                 - libraries for the core implementation
     /modules             - module
     /Examples            - project examples
     /thirdparty-licenses - thirdparty licenses
     AOR-Simulator.exe    - run the simulator in windows
     AOR-Simulator.sh     - run the simulator in unix
     readme.htm           - directions for installing
     AOR-Simulator.jar    - the AOR-Simulator-jar

     -NOTICE: the content of an existing dist-folder will be deleted,
              except for the projects


1. To create a gui-distribution in the dist folder, you have to call:

          ant [build]

   - This creates a runnable gui version of the AOR-Java-Simulator in the 
     dist folder
   - if modules should be added, they have to exist in trunk/modules/
     as *.jar container (this build-file doesn't build modules, they have to 
     be created by external executions)
   - NOTICE: the projects folder will not be changed


2. Create a zip-file
   
   - it will be copy all the project examples from the Examples folder in the 
     trunk
   - NOTICE: the source folder from trunk has to be named 'Examples'
   - NOTICE: these projects are independent from your own projects in the 
             dist folder (if exists)

2.1. To create a distribution in a zip-file, you have to call:

          ant build-zip

   - creates a runnable version of the AOR-Java-Simulator in a zip package
   - the name of the zip and the root folder in the zip will be created by:

          AOR-JavaSim-{day}-{month}-{year}.zip

   - NOTICE: {day}-{month}-{year} is the date when the zip was created
   - EXAMPLE: AOR-JavaSim-01-August-2009.zip

2.2. To create a distribution in a zip-file with special version-prefix:

          ant build-zip -Ddist-version={version-number}

   - creates a runnable version of the AOR-Java-Simulator in a zip package
   - the name of the zip and the root folder in the zip will be created by:

          AOR-JavaSim-{version-number}.zip

   - EXAMPLE:
          
          ant build-zip -Ddist-version=0.7a

     creates a zip-file named: AOR-JavaSim-0.7a.zip

2.3. To create a distribution in a zip-file include the tools.jar to run the
     Simulator on a system without an installed JDK (only JRE):

          ant build-zip-with-compiler

          or

          ant build-zip-with-compiler -Ddist-version={version-number}
