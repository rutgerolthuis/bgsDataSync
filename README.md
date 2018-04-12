# bgsDataSync
BGS data sync tool for our Elite Dangerous wing

start app: 
java -jar bgs-data-app.jar 

start app with other port than 8080: 
java -jar bgs-data-app.jar --server.port=<portnumber>

stop app: 
java -jar bgs-data-app.jar shutdown 
or 
java -jar bgs-data-app.jar shutdown --server.port=<portnumber> 

logging: 
logging is done in logs folder. Logfile will be rotated

running fire and forget: use javaw instead of java

call: 
http://localhost:<portnumber>/BGSSync?range=<number>

health: 
http://localhost:<portnumber>/actuator/health