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

/* Orekit tutorial for special event detection.
 * <p>This tutorial shows how to easily check for visibility between a satellite and a ground station.<p>
 * @author Pascal Parraud
 */
public class Satellite {

    /** Program entry point.
     * @param args program arguments (unused here)
     */
    public static void main(String[] args) {
        try {

            // configure Orekit
            AutoconfigurationCustom.configureOrekit();

            //  Initial state definition : date, orbit
            AbsoluteDate targetDate = new AbsoluteDate(2015, 12, 15, 2, 54, 27.000, TimeScalesFactory.getUTC());
//*******/            double mu =  3.986004415e+14; // gravitation coefficient
//*******/            Frame inertialFrame = FramesFactory.getEME2000(); // inertial frame for orbit definition
//*******/            Vector3D position  = new Vector3D(-6142438.668, 3492467.560, -25767.25680);
//*******/            Vector3D velocity  = new Vector3D(505.8479685, 942.7809215, 7435.922231);
//*******/            PVCoordinates pvCoordinates = new PVCoordinates(position, velocity);
//*******/            Orbit initialOrbit = new KeplerianOrbit(pvCoordinates, inertialFrame, initialDate, mu);
            String Line1 = "1 25544U 98067A   15348.82280235  .00015563  00000-0  23610-3 0  9996";
            String Line2 = "2 25544  51.6445 262.5935 0007865 276.8969 187.2494 15.54770155976144";
            TLE TLEdata = new TLE(Line1, Line2);
            
            // Propagator : consider a simple keplerian motion (could be more elaborate)
            Propagator TLEProp = TLEPropagator.selectExtrapolator(TLEdata);
            
//            // Earth and frame
//            Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
//            BodyShape earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
//                                                   Constants.WGS84_EARTH_FLATTENING,
//                                                   earthFrame);
//
//            // Station
//            final double longitude = FastMath.toRadians(89.);
//            final double latitude  = FastMath.toRadians(-8);
//            final double altitude  = 0.;
//            final GeodeticPoint station1 = new GeodeticPoint(latitude, longitude, altitude);
//            final TopocentricFrame sta1Frame = new TopocentricFrame(earth, station1, "station1");
//
//            // Event definition
//            final double maxcheck  = 60.0;
//            final double threshold =  0.001;
//            final double elevation = FastMath.toRadians(5.0);
//            final EventDetector sta1Visi =
//                    new ElevationDetector(maxcheck, threshold, sta1Frame).
//                    withConstantElevation(elevation).
//                    withHandler(new VisibilityHandler());

            // Add event to be detected
            //kepler.addEventDetector(sta1Visi);

            //Propagate from the initial date to the first raising or for the fixed duration
            //SpacecraftState finalState = kepler.propagate(initialDate.shiftedBy(1500.));

            //System.out.println(" Final state : " + finalState.getDate().durationFrom(initialDate));
            
            String stateVector = TLEProp.propagate(targetDate).getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true)).toString();
            
            stateVector = stateVector.replace("{", "").replace("}", ""); //Removes brackets {}
            stateVector = stateVector.replace("(", "").replace(")", ""); //Removes parentheses ()
            stateVector = stateVector.replace("P", "").replace("V", "").replace("A", ""); //Removes P, V, A
            stateVector = stateVector.replace(" ", ""); //Removes spaces
            String[] lineData = stateVector.split(",");
            
            String timeStamp = new String(lineData[0]);
            double[] position = new double[] {Double.parseDouble(lineData[1]), Double.parseDouble(lineData[2]), Double.parseDouble(lineData[3])};
            double[] velocity = new double[] {Double.parseDouble(lineData[4]), Double.parseDouble(lineData[5]), Double.parseDouble(lineData[6])};
            double[] acceleration = new double[] {Double.parseDouble(lineData[7]), Double.parseDouble(lineData[8]), Double.parseDouble(lineData[9])};
            
            position = Convert_To_Lat_Long(position);
            
            System.out.format("Latitude %.8f N%n" ,position[0]);
            System.out.format("Longitude %.8f E%n" ,position[1]);
            System.out.format("Altitude %.0f m %n" ,position[2]);
            

        } catch (OrekitException oe) {
            System.err.println(oe.getMessage());
        }
    }

    public static double[] Convert_To_Lat_Long(double[] posVec){
    	double Xcomp = posVec[0];
    	double Ycomp = posVec[1];
    	double Zcomp = posVec[2];
    	
    	
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
    	double EPR = 6356800; //Earth Polar Radius in meters
    	double EER = 6378100; //Earth Equator Radius in meters
    	
    	double earthRadius = Math.sqrt((Math.pow(EPR*EPR*Math.cos(latitude),2) + Math.pow(EER*EER*Math.cos(latitude),2))/(Math.pow(EPR*Math.cos(latitude),2) + Math.pow(EER*Math.cos(latitude),2)));
    	double orbitRadius = Math.sqrt(Xcomp*Xcomp + Ycomp*Ycomp + Zcomp*Zcomp);
    	altitude = orbitRadius - earthRadius;
    	return new double[]{latitude, longitude, altitude};
    }
    
    /** Handler for visibility event. */
    private static class VisibilityHandler implements EventHandler<ElevationDetector> {

        public Action eventOccurred(final SpacecraftState s, final ElevationDetector detector,
                                    final boolean increasing) {
            if (increasing) {
                System.out.println(" Visibility on " + detector.getTopocentricFrame().getName()
                                                     + " begins at " + s.getDate());
                return Action.CONTINUE;
            } else {
                System.out.println(" Visibility on " + detector.getTopocentricFrame().getName()
                                                     + " ends at " + s.getDate());
                return Action.STOP;
            }
        }

        public SpacecraftState resetState(final ElevationDetector detector, final SpacecraftState oldState) {
            return oldState;
        }

    }

}