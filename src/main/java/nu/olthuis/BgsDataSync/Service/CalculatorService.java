package nu.olthuis.BgsDataSync.Service;

class CalculatorService {
    static double calculateDistance(double ax, double ay, double az, double bx, double by, double bz) {

        return Math.sqrt(Math.pow(ax - bx,2) + Math.pow(ay - by,2) + Math.pow(az-bz,2));
        //http://www.wisfaq.nl/show3archive.asp?id=5578&j=2002

    }
}
