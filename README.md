# bgsDataSync
BGS data sync tool for our Elite Dangerous wing

start app:
from  the bgs-data-app folder:
java -Dlog4j.configurationFile=config\log4j2.xml  -jar lib\bgs-data-app.jar 
or javaw -Dlog4j.configurationFile=config\log4j2.xml  -jar lib\bgs-data-app.jar 
(prompt returns and no logging in console. you can close the console)

portnumber: 
in config/application.properties the server.port number is defined. 
Change this if the app needs to run on a different port. 

stop app: 
java -jar bgs-data-app.jar shutdown 
or 
java  -Dlog4j.configurationFile=config\log4j2.xml -jar bgs-data-app.jar shutdown --server.port=<portnumber> 
(the portdown for the shutdown cannot be fetched from the properties file)

alternate method:
use postman or other http request tool and perform a POST to
http://localhost:<portnumber>/actuator/shutdown

logging: 
logging is done in logs folder. Logfile will be rotated

calling the app: 
http://localhost:<portnumber>/BGSSync?range=<number>

health: 
http://localhost:<portnumber>/actuator/health

