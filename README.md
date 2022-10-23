# DockThor
Dock your bike faster than light

## Installation
Set your JAVA home in  gradle.properties
org.gradle.java.home=$PATHTOYOURJDK17

## Change location
if change location in the emulator doesn't work for your
launch adb-server and set in preferences adb server port to 5038
~/Android/Sdk/platform-tools/adb -P 5038 start-server
telnet  localhost 5554
auth token # token=(cat ~/.emulator_console_auth_token)
geo fix -73.9923318 40.6839783  # station1
geo fix -73.999231 40.718924 # station2
