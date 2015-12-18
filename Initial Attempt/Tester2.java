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

import Quick_Copy_Files.AutoconfigurationCustom;

/** Orekit tutorial for special event detection.
 * <p>This tutorial shows how to easily check for visibility between a satellite and a ground station.<p>
 * @author Pascal Parraud
 */
public class Tester2 {
	
	static CelestialBody sun;
	static double nightTimeAngle;
	static double centerStationRadius;

    /** Program entry point.
     * @param args program arguments (unused here)
     */
    public static void main(String[] args) {
        try {       	
        	String line1 = "1 25544U 98067A   15352.16254196  .00015510  00000-0  23433-3 0  9990";
            String line2 = "2 25544  51.6438 245.9260 0008096 292.2441 158.1233 15.54884251976664";
            
            // configure Orekit
        	AutoconfigurationCustom.configureOrekit();
        	
        	//SUNLocation
			sun  = CelestialBodyFactory.getSun();

            //  Initial state definition : date, orbit
            AbsoluteDate targetDate = new AbsoluteDate(2015, 12, 18, 0, 0, 01.000, TimeScalesFactory.getUTC());
            targetDate = targetDate.shiftedBy(3600*48);
            
            
            
            Vector3D sunPos = sun.getPVCoordinates(targetDate, FramesFactory.getITRF(IERSConventions.IERS_2010, true)).getPosition();
            nightTimeAngle = FastMath.PI/2 + FastMath.asin((Constants.SUN_RADIUS)/(sunPos.getNorm()));
            //nightTimeAngle = Math.asin((696300000)/(sunPos.getNorm()));
            
            //  Initial TLE orbit data
            TLE TLEdata = new TLE(line1, line2);
            
            // Propagator : using TLE elements
            Propagator TLEProp = TLEPropagator.selectExtrapolator(TLEdata);
            
            //*******************************************************************************************
            //                          will be part of constructor                                     *
            // Earth and frame                                                                          *
            Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
            BodyShape earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                                                   Constants.WGS84_EARTH_FLATTENING,
                                                   earthFrame);

            // Station                                                                                   *
            final double radLongitude = FastMath.toRadians(-81.503333);
            final double radLatitude  = FastMath.toRadians(27.594444);
            final double altitude  = 0;
            final GeodeticPoint station1 = new GeodeticPoint(radLatitude, radLongitude, altitude);
            final TopocentricFrame sta1Frame = new TopocentricFrame(earth, station1, "station1");
            centerStationRadius = sta1Frame.getRange(new Vector3D(0,0,0), earthFrame, targetDate);

            // Event definition                                                                          *
            final double maxcheck  = 60.0;
            final double threshold =  0.001;
            final double elevation = FastMath.toRadians(10.0);
            final EventDetector sta1Visi =
                    new ElevationDetector(maxcheck, threshold, sta1Frame).
                    withConstantElevation(elevation).
                    withHandler(new VisibilityHandler());
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            //                                     will be part of constructor                           *

            // Add event to be detected
            TLEProp.addEventDetector(sta1Visi);

            // Propagate from the initial date to the first raising or for the fixed duration
            SpacecraftState finalState = TLEProp.propagate(targetDate);

            System.out.println(" Final state : " + finalState.getDate().durationFrom(targetDate));

        } catch (OrekitException oe) {
        	System.err.println(oe.getMessage());
        }
    }

    /** Handler for visibility event. */
    private static class VisibilityHandler implements EventHandler<ElevationDetector> {

        public Action eventOccurred(final SpacecraftState s, final ElevationDetector detector,
                                    final boolean increasing) {
            if (increasing) {
                if(true){//isNightTime(s)){
                	System.out.println(" Visibility on " + detector.getTopocentricFrame().getName()
                                                     + " begins at " + s.getDate().shiftedBy(-3600*5));
                	try{
                		System.out.println("\t" + FastMath.toDegrees(detector.getTopocentricFrame().getAzimuth(s.getPVCoordinates().getPosition(), s.getFrame(), s.getDate())));
                	}catch (OrekitException oe){
                		System.out.println("FAILED");
                	}
                	
                
                }
                return Action.CONTINUE;
            } else {
                System.out.println(" Visibility on " + detector.getTopocentricFrame().getName()
                                                     + " ends at " + s.getDate().shiftedBy(-3600*5));
                //return Action.STOP;
                return Action.CONTINUE;
            }
        }

        public SpacecraftState resetState(final ElevationDetector detector, final SpacecraftState oldState) {
            return oldState;
        }
    }

    private static boolean isNightTime(final SpacecraftState s){
    	Vector3D curSunPos;
		try {
			curSunPos = sun.getPVCoordinates(s.getPVCoordinates().getDate(), FramesFactory.getITRF(IERSConventions.IERS_2010, true)).getPosition();
			Vector3D curSatPos = s.getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true)).getPosition();		
	    	double angle = Vector3D.dotProduct(curSunPos, curSatPos) / (curSunPos.getNorm()*curSatPos.getNorm());
	    	if(angle < 0){angle = FastMath.PI - angle;}
	    	//System.out.println("the DarkAngle is " + nightTimeAngle);
	    	//System.out.println("the angle is " + angle);
	    	
	    	return angle > nightTimeAngle;
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