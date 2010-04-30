# This is the start script of the AOR Simulator GUI for linux/unix compatible systems.
#
# If your JAVA_HOME environment variable is not set yet, then uncomment 
# the following line. Depending on your linux distribution you may need
# to modify the path to your Java Standard Development Kit (SDK)
#
# export JAVA_HOME=/usr/java/default
#
# An other path are may: /usr/java/jdk1.5.0_16 
#
# If you are not shure about the right path go to the common Java 
# directory and have a look.
#
# cd /user/java/
# ls
#  
# Now you should see your java installation direcory. If not then download 
# the Java 6 SDK as RPM version and install it with root rights.

$JAVA_HOME/bin/java -jar AOR-Simulator.jar -Xmx128
