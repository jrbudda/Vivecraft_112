/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jopenvr;

import de.fruitfly.ovr.structs.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import jopenvr.HmdMatrix34_t;
import jopenvr.HmdMatrix44_t;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Quaternion;

/**
 *
 * @author reden
 */
public class OpenVRUtil {

    private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(4);
    private static final long SPIN_YIELD_PRECISION = TimeUnit.MILLISECONDS.toNanos(2);
    
    public static void sleepNanos(long nanoDuration) {
        final long end = System.nanoTime() + nanoDuration; 
        long timeLeft = nanoDuration; 
        do { 
            try {
                if (timeLeft > SLEEP_PRECISION) {
                    Thread.sleep(1); 
                } else if (timeLeft > SPIN_YIELD_PRECISION) {
                    Thread.sleep(0); 
                }
            } catch(Exception e) { }
            timeLeft = end - System.nanoTime(); 
        } while (timeLeft > 0); 
    }

    // VIVE START
    public static void Matrix4fSet(Matrix4f mat, float m11, float m12, float m13, float m14, float m21, float m22, float m23, float m24, float m31, float m32, float m33, float m34, float m41, float m42, float m43, float m44)
    {
        mat.M[0][0] = m11;
        mat.M[0][1] = m12;
        mat.M[0][2] = m13;
        mat.M[0][3] = m14;
        mat.M[1][0] = m21;
        mat.M[1][1] = m22;
        mat.M[1][2] = m23;
        mat.M[1][3] = m24;
        mat.M[2][0] = m31;
        mat.M[2][1] = m32;
        mat.M[2][2] = m33;
        mat.M[2][3] = m34;
        mat.M[3][0] = m41;
        mat.M[3][1] = m42;
        mat.M[3][2] = m43;
        mat.M[3][3] = m44;
    }

    public static void Matrix4fCopy(Matrix4f source, Matrix4f dest)
    {
        dest.M[0][0] = source.M[0][0];
        dest.M[0][1] = source.M[0][1];
        dest.M[0][2] = source.M[0][2];
        dest.M[0][3] = source.M[0][3];
        dest.M[1][0] = source.M[1][0];
        dest.M[1][1] = source.M[1][1];
        dest.M[1][2] = source.M[1][2];
        dest.M[1][3] = source.M[1][3];
        dest.M[2][0] = source.M[2][0];
        dest.M[2][1] = source.M[2][1];
        dest.M[2][2] = source.M[2][2];
        dest.M[2][3] = source.M[2][3];
        dest.M[3][0] = source.M[3][0];
        dest.M[3][1] = source.M[3][1];
        dest.M[3][2] = source.M[3][2];
        dest.M[3][3] = source.M[3][3];
    }

    public static Matrix4f Matrix4fSetIdentity(Matrix4f mat)
    {
        mat.M[0][0] = mat.M[1][1] = mat.M[2][2] = mat.M[3][3] = 1.0F;
        mat.M[0][1] = mat.M[1][0] = mat.M[2][3] = mat.M[3][1] = 0.0F;
        mat.M[0][2] = mat.M[1][2] = mat.M[2][0] = mat.M[3][2] = 0.0F;
        mat.M[0][3] = mat.M[1][3] = mat.M[2][1] = mat.M[3][0] = 0.0F;
        return mat;
    }
        
    public static Matrix4f convertSteamVRMatrix3ToMatrix4f(HmdMatrix34_t hmdMatrix, Matrix4f mat){
        Matrix4fSet(mat,
                hmdMatrix.m[0], hmdMatrix.m[1], hmdMatrix.m[2], hmdMatrix.m[3],
                hmdMatrix.m[4], hmdMatrix.m[5], hmdMatrix.m[6], hmdMatrix.m[7],
                hmdMatrix.m[8], hmdMatrix.m[9], hmdMatrix.m[10], hmdMatrix.m[11],
                0f, 0f, 0f, 1f
        );
        return mat;
    }
    
    public static Matrix4f convertSteamVRMatrix4ToMatrix4f(HmdMatrix44_t hmdMatrix, Matrix4f mat)
    {
        Matrix4fSet(mat, hmdMatrix.m[0], hmdMatrix.m[1], hmdMatrix.m[2], hmdMatrix.m[3],
                hmdMatrix.m[4], hmdMatrix.m[5], hmdMatrix.m[6], hmdMatrix.m[7],
                hmdMatrix.m[8], hmdMatrix.m[9], hmdMatrix.m[10], hmdMatrix.m[11],
                hmdMatrix.m[12], hmdMatrix.m[13], hmdMatrix.m[14], hmdMatrix.m[15]);
        return mat;
    }

    public static Vector3f convertMatrix4ftoTranslationVector(Matrix4f mat) {
        return new Vector3f(mat.M[0][3], mat.M[1][3], mat.M[2][3]);
    }

    public static Quatf convertMatrix4ftoRotationQuat(Matrix4f mat) {
        return JMonkeyHelpers.convertMatrix4ftoRotationQuat(
                mat.M[0][0],mat.M[0][1],mat.M[0][2],
                mat.M[1][0],mat.M[1][1],mat.M[1][2],
                mat.M[2][0],mat.M[2][1],mat.M[2][2]);
    }

    public static Matrix4f rotationXMatrix(float angle) {
        float sina = (float) Math.sin((double)angle);
        float cosa = (float) Math.cos((double)angle);
        return new Matrix4f(1.0F, 0.0F, 0.0F,
                            0.0F, cosa, -sina,
                            0.0F, sina, cosa);
    }

    public static Matrix4f rotationZMatrix(float angle) {
        float sina = (float) Math.sin((double)angle);
        float cosa = (float) Math.cos((double)angle);
        return new Matrix4f(cosa, -sina, 0.0F,
                sina, cosa, 0.0f,
                0.0F, 0.0f, 1.0f);
    }

    public static EulerOrient getEulerAnglesDegYXZ(Quatf q) {
        EulerOrient eulerAngles = new EulerOrient();

        eulerAngles.yaw = (float)Math.toDegrees(Math.atan2( 2*(q.x*q.z + q.w*q.y), q.w*q.w - q.x*q.x - q.y*q.y + q.z*q.z ));
        eulerAngles.pitch = (float)Math.toDegrees(Math.asin ( -2*(q.y*q.z - q.w*q.x) ));
        eulerAngles.roll = (float)Math.toDegrees(Math.atan2( 2*(q.x*q.y + q.w*q.z), q.w*q.w - q.x*q.x + q.y*q.y - q.z*q.z ));

        return eulerAngles;
    }
    // VIVE END

    public static long getNativeWindow() {
        long window = -1;
        try {
            Object displayImpl = null;
            Method[] displayMethods = Display.class.getDeclaredMethods();
            for (Method m : displayMethods) {
                if (m.getName().equals("getImplementation")) {
                    m.setAccessible(true);
                    displayImpl = m.invoke(null, (Object[]) null);
                    break;
                }
            }            
            String fieldName = null;
            switch (LWJGLUtil.getPlatform()) {
                case LWJGLUtil.PLATFORM_LINUX:
                    fieldName = "current_window";
                    break;
                case LWJGLUtil.PLATFORM_WINDOWS:
                    fieldName = "hwnd";
                    break;
            }
            if (null != fieldName) {
                Field[] windowsDisplayFields = displayImpl.getClass().getDeclaredFields();
                for (Field f : windowsDisplayFields) {
                    if (f.getName().equals(fieldName)) {
                        f.setAccessible(true);
                        window = (Long) f.get(displayImpl);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return window;
    }
}
