/* Copyright 2002-2015 CS Systèmes d'Information
 * Licensed to CS Systèmes d'Information (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Locale;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.EclipseDetector;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.propagation.sampling.OrekitStepHandler;
import org.orekit.propagation.sampling.OrekitStepInterpolator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;

import Quick_Copy_Files.AutoconfigurationCustom;

/** Orekit tutorial for special event detection.
 * <p>This tutorial shows how to easily check for visibility between a satellite and a ground station.<p>
 * @author Pascal Parraud
 */
public class Tester2 {
	
	static CelestialBody sun;
	static CelestialBody earth;
	static Frame earthFrame;
	/////Consider deleting/////static double nightTimeAngle;
	/////Consider deleting/////static double centerStationRadius;
	static Propagator TLEProp;
	static TopocentricFrame groundstationFrame;

    /** Program entry point.
     * @param args program arguments (unused here)
     */
    public static void main(String[] args) {
        try {       	
        	String line7 = "1 25544U 98067A   15352.16254196  .00015510  00000-0  23433-3 0  9990";
            String line8 = "2 25544  51.6438 245.9260 0008096 292.2441 158.1233 15.54884251976664";
            
            // configure Orekit
        	AutoconfigurationCustom.configureOrekit();
        	
        	//SUNLocation
			sun  = CelestialBodyFactory.getSun();
			earth = CelestialBodyFactory.getEarth();

            //  Initial state definition : date, orbit
            AbsoluteDate targetDate = new AbsoluteDate(2015, 12, 18, 0, 0, 01.000, TimeScalesFactory.getUTC());
            targetDate = targetDate.shiftedBy(3600*48);
            
            /////Consider deleting/////Vector3D sunPos = sun.getPVCoordinates(targetDate, this.earthFrame).getPosition();
            /////Consider deleting/////nightTimeAngle = FastMath.PI/2 + FastMath.asin((Constants.SUN_RADIUS)/(sunPos.getNorm()));
            
            
            
            //*******************************************************************************************
            //                          will be part of constructor                                     *
            // Event definition                                                                          *
            final double maxcheck  = 60.0;
            final double threshold =  0.001;
            final double elevation = FastMath.toRadians(10.0);
            final EventDetector sta1Visi =
                    new ElevationDetector(maxcheck, threshold, groundstationFrame).
                    withConstantElevation(elevation).
                    withHandler(new VisibilityHandler());
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            //                                     will be part of constructor                           *

            // Add Elevation detector
            TLEProp.addEventDetector(sta1Visi);
            
//            // Add Elevation detector
//            final EventDetector thing = 
//            		new EclipseDetector(sun, Constants.SUN_RADIUS, earth, Constants.WGS84_EARTH_EQUATORIAL_RADIUS).
//            		withHandler(new DarknessHandler()).
//            		withUmbra();
//            TLEProp.addEventDetector(thing);
            

            //TLEProp.setMasterMode(new TutorialStepHandler());
            

            // Propagate from the initial date to the first raising or for the fixed duration
            SpacecraftState finalState = TLEProp.propagate(targetDate);

            System.out.println(" Final state : " + finalState.getDate().durationFrom(targetDate));
            
        } catch (OrekitException oe) {
        	System.err.println(oe.getMessage());
        }
    }
    
    public Tester2(String line1, String line2, CelestialBody sun, CelestialBody earth, double latitude, double longitude, double altitude){
    	
        try {
        	// Initial TLE orbit data
			TLE TLEdata = new TLE(line1, line2);
			
			// Propagator : using TLE elements
	        this.TLEProp = TLEPropagator.selectExtrapolator(TLEdata);
	        
	        // Define bodies
	        this.sun = sun;
	        this.earth = earth;
	        this.earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
	        
	        // Set the Ground Station location
	        SetGSLocation(latitude,  longitude, altitude);
	        
		} catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static boolean QuickCheck(double latitude, double longitude, double altitude){
    	// Station                                                                                   *
        final double radLongitude = FastMath.toRadians(longitude);
        final double radLatitude  = FastMath.toRadians(latitude);
    	
    	
    	
    	
    	return true;
    }

    public void SetGSLocation(double longitude, double latitude, double altitude){
    	BodyShape earthBody = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
		                                       Constants.WGS84_EARTH_FLATTENING,
		                                       this.earthFrame);

		// Station                                                                                   
		final double radLongitude = FastMath.toRadians(longitude);
		final double radLatitude  = FastMath.toRadians(latitude);
		final GeodeticPoint station1 = new GeodeticPoint(radLatitude, radLongitude, altitude);
		groundstationFrame = new TopocentricFrame(earthBody, station1, "station1");
    }
    
    /** Handler for visibility event. */
    private class VisibilityHandler implements EventHandler<ElevationDetector> {

        public Action eventOccurred(final SpacecraftState s, final ElevationDetector detector,
                                    final boolean increasing) {
            //System.out.println("\t\t\t" + s.getDate());
        	
            System.out.println(isNightTime(s, detector) + " It is dark on Earth");
            if (increasing) {
                if(true){//isNightTime(s)){
                	System.out.println(" Visibility on " + detector.getTopocentricFrame().getName()
                                                     + " begins at " + s.getDate().shiftedBy(-3600*8));
//                	try{
//                		System.out.println("\t" + FastMath.toDegrees(detector.getTopocentricFrame().getAzimuth(s.getPVCoordinates().getPosition(), s.getFrame(), s.getDate())));
//                	}catch (OrekitException oe){
//                		System.out.println("FAILED");
//                	}
                }
                return Action.CONTINUE;
            } else {
                System.out.println(" Visibility on " + detector.getTopocentricFrame().getName()
                                                     + " ends at " + s.getDate().shiftedBy(-3600*8));
                //return Action.STOP;
                return Action.CONTINUE;
            }
        }

        public SpacecraftState resetState(final ElevationDetector detector, final SpacecraftState oldState) {
            return oldState;
        }
    }

    private static class DarknessHandler implements EventHandler<EclipseDetector> {
                
        public Action eventOccurred(final SpacecraftState s, final EclipseDetector detector, final boolean increasing) {
    		if (increasing) {
            	System.out.println("Into Full Eclipse Darkness " + s.getDate() + " --------------------------------------");
    			//output.add(s.getDate() + ": switching to day-night rdv 1 law");
                //System.out.println("# " + (s.getDate().durationFrom(AbsoluteDate.J2000_EPOCH) / Constants.JULIAN_DAY) + " eclipse-entry day-night-rdv1-mode");
                //endDayNightRdV1Event_increase.addEventDate(s.getDate().shiftedBy(40));
                //endDayNightRdV1Event_decrease.addEventDate(s.getDate().shiftedBy(40));
            }
    		else {
    			System.out.println("Leaving Full Eclipse Darkness " + s.getDate() + " +++++++++++++++++++++++++++++++++++");
    		}
    		return Action.CONTINUE;
        }
        
        public SpacecraftState resetState(EclipseDetector detector, SpacecraftState oldState) {
        	return oldState;
        }
    }

    private static class TutorialStepHandler implements OrekitStepHandler {

        private TutorialStepHandler() {
            //private constructor
        }

        public void init(final SpacecraftState s0, final AbsoluteDate t) {
            System.out.println("          date                a           e" +
                               "           i         \u03c9          \u03a9" +
                               "          \u03bd");
        }

		public void handleStep(OrekitStepInterpolator o, boolean isLast)
				throws PropagationException {
			System.out.println("\t\t\t" + (o.getCurrentDate().durationFrom(o.getPreviousDate())));
			
		}
    }
    
    private boolean isNightTime(final SpacecraftState s, ElevationDetector detector){  
    	Vector3D curSunPos;
		try {
			// origin is the center of the Earth
			curSunPos = sun.getPVCoordinates(s.getDate(), this.earthFrame).getPosition();
			Vector3D curSatPos = s.getPVCoordinates(this.earthFrame).getPosition();		
	    	
			// origin has been offset to the ground station
			Vector3D stationToSun = curSunPos.subtract(curSatPos);
	    	Vector3D stationZenith =  detector.getTopocentricFrame().getZenith();
	    			
	    	double angle = Vector3D.angle(stationToSun, stationZenith); // Sun center to station to zenith
	    	double sunAngleRadius = FastMath.atan(Constants.SUN_RADIUS/stationToSun.getNorm());
	    	//System.out.println("\nthe DarkAngle is " + nightTimeAngle);
	    	//System.out.println("the angle is " + angle);
	    	
	    	return angle-sunAngleRadius > Math.PI/2;
		} catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("This broke");
			return false;
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
    			longitude = FastMath.toDegrees(FastMath.atan(Ycomp/Xcomp));
    		}
    		else{
    			longitude = 180 - FastMath.toDegrees(FastMath.atan(FastMath.abs(Ycomp/Xcomp)));
    		}
    	}
    	else{
    		if(Xcomp > 0){
    			longitude = -1 * FastMath.toDegrees(FastMath.atan(FastMath.abs(Ycomp/Xcomp)));
    		}
    		else{
    			longitude = -1 * (180 - FastMath.toDegrees(FastMath.atan(Ycomp/Xcomp)));
    		}
    	}
    	
    	//Calculate latitude
    	latitude = FastMath.toDegrees(FastMath.atan(Zcomp/FastMath.sqrt(Xcomp*Xcomp + Ycomp*Ycomp)));
    	
    	//Calculate radius and altitude
    	double EER = Constants.WGS84_EARTH_EQUATORIAL_RADIUS; //Earth Equator Radius in meters
    	double EPR = EER - EER*Constants.WGS84_EARTH_FLATTENING; //Earth Polar Radius in meters
    	
    	double earthRadius = FastMath.sqrt((FastMath.pow(EPR*EPR*FastMath.cos(latitude),2) + FastMath.pow(EER*EER*FastMath.cos(latitude),2))/(FastMath.pow(EPR*FastMath.cos(latitude),2) + FastMath.pow(EER*FastMath.cos(latitude),2)));
    	double orbitRadius = FastMath.sqrt(Xcomp*Xcomp + Ycomp*Ycomp + Zcomp*Zcomp);
    	altitude = orbitRadius - earthRadius;
    	
    	return new double[]{latitude, longitude, altitude};
    }
}
