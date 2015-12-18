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
	//inputs
	//AbsoluteDate now;
	//AbsoluteDate endDate;
	String line1;
	String line2;
	
	//outputs
	String satName; //printed
	String satID; //printed
	AbsoluteDate localRise; //printed
	//Rise location
	AbsoluteDate localSet; //printed
	//Set location
	double maxElevation; //printed
	AbsoluteDate maxElevationTime; //printed
	

    public Satellite(String name, String line1, String line2){
    	this.satName = name;
    	this.line1 = line1;
    	this.line2 = line2;
    	this.satID = (line2.split(" "))[1];
    }

    public boolean quickCheck(AbsoluteDate endDate, CelestialBody sun) throws OrekitException {
        //  Initial TLE orbit data
        TLE TLEdata = new TLE(line1, line2);
        
        // Propagator : using TLE elements
        Propagator TLEProp = TLEPropagator.selectExtrapolator(TLEdata);
        
        Vector3D posVec = TLEProp.propagate(endDate).getPVCoordinates(FramesFactory.getITRF(IERSConventions.IERS_2010, true)).getPosition();
        
        double[] posLatLong = Convert_To_Lat_Long(posVec);
        
        System.out.format("Latitude %.8f N%n" ,posLatLong[0]);
        System.out.format("Longitude %.8f E%n" ,posLatLong[1]);
        System.out.format("Altitude %.0f m %n" ,posLatLong[2]);
        
        return true;
    }
    
    //PRINTABLE_RISETIME = localRise.getComponents(TimeScalesFactory.getUTC()).getTime().toString();

    public static double[] Convert_To_Lat_Long(Vector3D posVec){
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