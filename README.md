# SPTanalysis
SPTanalysis plugin for advanced analysis single-particle trajectories in ImageJ

# Running the project in Eclipse:
1. Import the project:
Launch eclipse and go to File -> Open Projects From File System
And point it to the SPTanalysis folder in this directory.

2. Generating the jar file:
Run -> Run Configurations -> Maven build -> New configuration
In goals enter:
compile install
Add "-DskipTests" if you want faster generation of the jar file.

Add the following parameter so that maven directly copies the generated jar file in ImageJ's folder:
Name: scijava.app.directory
Value: path to your Fiji install (eg. /home/pierre/Fiji.app)

# Documentation
https://docs.google.com/document/d/12a4hNXNEbJDkbb1czrA6oWm2YvU80d-FFRzxe6pkhtQ/edit?usp=sharing
