
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


public class Primary {

    /** Program entry point.
     * @param args program arguments (unused here)
     */	
	
	public static void main(String[] args) {
        try {

            // configure Orekit
            AutoconfigurationCustom.configureOrekit();

            //  Initial state definition : date, orbit
            AbsoluteDate targetDate = new AbsoluteDate(2015, 12, 15, 2, 54, 27.000, TimeScalesFactory.getUTC());
            
            //SUNLocation
			CelestialBody sun  = CelestialBodyFactory.getSun();
			Vector3D sunPos = sun.getPVCoordinates(targetDate, FramesFactory.getITRF(IERSConventions.IERS_2010, true)).getPosition();
			System.out.println(sunPos.getNorm());

        } catch (OrekitException oe) {
            System.err.println(oe.getMessage());
        }
    }
}