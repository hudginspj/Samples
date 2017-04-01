package com.parrot.sdksample.dynamics;

import android.util.Log;

import com.parrot.arsdk.arcontroller.ARCONTROLLER_DICTIONARY_KEY_ENUM;
import com.parrot.arsdk.arcontroller.ARControllerArgumentDictionary;
import com.parrot.arsdk.arcontroller.ARControllerDictionary;
import com.parrot.arsdk.arcontroller.ARDeviceController;
import com.parrot.arsdk.arcontroller.ARFeatureARDrone3;

/**
 * Created by pjhud on 3/31/2017.
 */

public class DynamicsUtilities {
    private static final String TAG = "DynamicsUtilities";

    public static byte flag = 0;
    public static byte roll = 0;
    public static byte pitch = 0;
    public static byte yaw = 0;
    public static byte gaz = 0;
    public static byte timestampAndSeqNum = 0;

    private static double maxTiltInRadians = Math.toRadians(15);

    public static double droneZ = 0.0;
    public static double calDroneZ = 0.0;
    public static double viewX = 0.0;
    public static double viewY = 0.0;
    public static double viewZ = 0.0;



    public static void calibrate(){
        calDroneZ = droneZ;
    }



    public static void calcPitchRoll(double nominalPitchInRadians, byte thetaRightFromCameraInRadians) {
        //double nomPitch = maxTiltInRadians * nominalPitch / 100.0;
        System.out.println("nomPitchInDegrees" + Math.toDegrees(nominalPitchInRadians));
        double f = Math.tan(-nominalPitchInRadians);
        double pitchInRadians = -Math.atan(f * Math.cos(thetaRightFromCameraInRadians));
        double rollInRadians = Math.atan(f * Math.sin(thetaRightFromCameraInRadians));
        //System.out.println(pitchInRadians);
        pitch = (byte) (100.0 * pitchInRadians / maxTiltInRadians) ;
        roll = (byte) (100.0 * rollInRadians / maxTiltInRadians);
        System.out.println("pitch: " + pitch + " / roll: " + roll);
    }

    public static void calcSlaveYaw() {
        double goLeftRad = normalizeRad(viewZ - (droneZ - calDroneZ));
        if (goLeftRad > 0.5) {
            yaw = -99;
        } else if (goLeftRad < -0.5) {
            yaw = 99;
        } else if (goLeftRad > 0.1) {
            yaw = -50;
        } else if (goLeftRad < -0.1) {
            yaw = 50;
        } else {
            yaw = 0;
        }
    }


    //add all this to BebopDrone.Listener
    public static void onCommandReceived (ARDeviceController deviceController, ARCONTROLLER_DICTIONARY_KEY_ENUM commandKey, ARControllerDictionary elementDictionary) {
        if ((commandKey == ARCONTROLLER_DICTIONARY_KEY_ENUM.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSTATE_ATTITUDECHANGED) && (elementDictionary != null)){
            ARControllerArgumentDictionary<Object> args = elementDictionary.get(ARControllerDictionary.ARCONTROLLER_DICTIONARY_SINGLE_KEY);
            if (args != null) {
                //Sets the pitch parameter. Range from -1.0 (pitch forward to euler_angle_max radians) to +1.0 (pitch back to euler_angle_max radians). 0.0 means level. Defaults at initialisation to 0.0. The new pitch parameter will be sent during the next update() if the drone is flying.
                float roll = (float)((Double)args.get(ARFeatureARDrone3.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSTATE_ATTITUDECHANGED_ROLL)).doubleValue();
                //Sets the roll parameter. Range from -1.0 (roll left to euler_angle_max radians) to +1.0 (roll right to euler_angle_max radians). 0.0 means level. Defaults at initialisation to 0.0. The new roll parameter will be sent during the next update() if the drone is flying.
                float pitch = (float)((Double)args.get(ARFeatureARDrone3.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSTATE_ATTITUDECHANGED_PITCH)).doubleValue();
                //Sets the yaw (rate of change of yaw). Range from -1.0 (rotate left at control_yaw radians/sec) to +1.0 (rotate right at control_yaw radians/sec). 0.0 means no rotation. Defaults at initialisation to 0.0. The new yaw parameter will be sent during the next update() if the drone is flying.
                float yaw = (float)((Double)args.get(ARFeatureARDrone3.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSTATE_ATTITUDECHANGED_YAW)).doubleValue();
            }
        }

        //MaxTiltInDegrees
        if ((commandKey == ARCONTROLLER_DICTIONARY_KEY_ENUM.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSETTINGSSTATE_MAXTILTCHANGED) && (elementDictionary != null)){
            ARControllerArgumentDictionary<Object> args = elementDictionary.get(ARControllerDictionary.ARCONTROLLER_DICTIONARY_SINGLE_KEY);
            if (args != null) {
                float current = (float)((Double)args.get(ARFeatureARDrone3.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSETTINGSSTATE_MAXTILTCHANGED_CURRENT)).doubleValue();
                float min = (float)((Double)args.get(ARFeatureARDrone3.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSETTINGSSTATE_MAXTILTCHANGED_MIN)).doubleValue();
                float max = (float)((Double)args.get(ARFeatureARDrone3.ARCONTROLLER_DICTIONARY_KEY_ARDRONE3_PILOTINGSETTINGSSTATE_MAXTILTCHANGED_MAX)).doubleValue();
            }
        }
    }

    public static void testCommand() {

        ARDeviceController deviceController = null; //new ARDeviceController(null);
        deviceController.getFeatureARDrone3().sendPilotingPCMD((byte)flag, (byte)roll, (byte)pitch, (byte)yaw, (byte)gaz, (int)timestampAndSeqNum);
    }


    public static void updateViewerAtt(double x, double y, double z) {
        Log.d(TAG, "updateViewerAtt: VIEWER X:" + x + " Y:" + y + " Z:" + z);
        viewX = x;
        viewY = y;
        viewZ = z;
    }

    public static void updateDroneAtt(double z) {
        Log.d(TAG, "updateDroneAtt: DRONE Z:" + z);
        droneZ = z;
    }

    public static double normalizeRad(double r) {
        while (r < -Math.PI) {
            r += 2* Math.PI;
        }
        while (r >= Math.PI) {
            r -= 2*Math.PI;
        }
        return r;
    }
}
