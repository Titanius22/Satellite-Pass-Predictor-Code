import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedPVCoordinates;

import Quick_Copy_Files.AutoconfigurationCustom;


public class tester {
	
	public static void main(String[] args) {
		
		try {
			String line1 = "1 25544U 98067A   15350.55003455  .00016684  00000-0  25199-3 0  9995";
            String line2 = "2 25544  51.6439 253.9717 0007894 283.6533 134.7262 15.54831942976416";
        	
        	// configure Orekit
            AutoconfigurationCustom.configureOrekit();
            
            System.out.println("1");
			
            //  Initial state definition : date, orbit
            AbsoluteDate targetDate = new AbsoluteDate(2015, 12, 16, 16, 25, 0.000, TimeScalesFactory.getUTC());

            System.out.println("2");
			
            //  Initial TLE orbit data
            TLE TLEdata = new TLE(line1, line2);

            System.out.println("3");
			
            // Propagator : consider a simple keplerian motion (could be more elaborate)
            Propagator TLEProp = TLEPropagator.selectExtrapolator(TLEdata);

            System.out.println("4");
			
            //Vector3D posVec = TLEProp.propagate(targetDate).getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true)).getPosition();
            TimeStampedPVCoordinates posVec = TLEProp.propagate(targetDate).getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true));
            //double[] posLatLong = Convert_To_Lat_Long(posVec);

            System.out.println("5");
			
            //SUNLocation
			CelestialBody sun  = CelestialBodyFactory.getSun();
			
			System.out.println("6");
			
			TimeStampedPVCoordinates sunData = sun.getPVCoordinates(targetDate, FramesFactory.getITRF(IERSConventions.IERS_2010, true));
			//System.out.println(sunPos.getNorm());
			long startTime = System.nanoTime();
			System.out.println(startTime);
			
			Vector3D sunPos1 = posVec.shiftedBy(600).getPosition();
			double[] sunPos1LatLong = Convert_To_Lat_Long(sunPos1);
			long time1 = System.nanoTime();
			System.out.println(time1);
			
			Vector3D sunPos2 = TLEProp.propagate(targetDate.shiftedBy(600)).getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true)).getPosition();
			double[] sunPos2LatLong = Convert_To_Lat_Long(sunPos2);
			long time2 = System.nanoTime();
			System.out.println(time2);
			
            System.out.println("11");
			
            System.out.format("Latitude %.8f N%n" ,sunPos1LatLong[0]);
            System.out.format("Longitude %.8f E%n" ,sunPos1LatLong[1]);
            System.out.format("Altitude %.0f m %n" ,sunPos1LatLong[2]);
            System.out.format("Latitude %.8f N%n" ,sunPos2LatLong[0]);
            System.out.format("Longitude %.8f E%n" ,sunPos2LatLong[1]);
            System.out.format("Altitude %.0f m %n" ,sunPos2LatLong[2]);
            System.out.format("Latitude delta %.8f N%n" ,sunPos1LatLong[0]-sunPos2LatLong[0]);
            System.out.format("Longitude delta %.8f E%n" ,sunPos1LatLong[1]-sunPos2LatLong[1]);
            System.out.format("Altitude delta %.0f m %n" ,sunPos1LatLong[2]-sunPos2LatLong[2]);
            System.out.format("%d %d %n", (time1-startTime),(time2-time1));
            //System.out.println(TLEProp.getFixedStepSize());
		
		} catch (OrekitException oe) {
            System.err.println(oe.getMessage());
        }
	}

    private static double[] Convert_To_Lat_Long(Vector3D posVec){
    	double Xcomp = posVec.getX();
    	double Ycomp = posVec.getY();
    	double Zcomp = posVec.getZ();
    	
    	double longitude;
    	double latitude;
    	double altitude;
    	
    	//Done so all cases of longitudes are right
    	if(Ycomp > 0){
    		if(Xcomp > 0){
    			longitude = Math.toDegrees(Math.atan(Ycomp/Xcomp));
    		}
    		else{
    			longitude = 180 - Math.toDegrees(Math.atan(Math.abs(Ycomp/Xcomp)));
    		}
    	}
    	else{
    		if(Xcomp > 0){
    			longitude = -1 * Math.toDegrees(Math.atan(Math.abs(Ycomp/Xcomp)));
    		}
    		else{
    			longitude = -1 * (180 - Math.toDegrees(Math.atan(Ycomp/Xcomp)));
    		}
    	}
    	
    	//Calculate latitude
    	latitude = Math.toDegrees(Math.atan(Zcomp/Math.sqrt(Xcomp*Xcomp + Ycomp*Ycomp)));
    	
    	//Calculate radius and altitude
    	double EER = Constants.WGS84_EARTH_EQUATORIAL_RADIUS; //Earth Equator Radius in meters
    	double EPR = EER - EER*Constants.WGS84_EARTH_FLATTENING; //Earth Polar Radius in meters
    	
    	double earthRadius = Math.sqrt((Math.pow(EPR*EPR*Math.cos(latitude),2) + Math.pow(EER*EER*Math.cos(latitude),2))/(Math.pow(EPR*Math.cos(latitude),2) + Math.pow(EER*Math.cos(latitude),2)));
    	double orbitRadius = Math.sqrt(Xcomp*Xcomp + Ycomp*Ycomp + Zcomp*Zcomp);
    	altitude = orbitRadius - earthRadius;
    	
    	return new double[]{latitude, longitude, altitude};
    }
	
}