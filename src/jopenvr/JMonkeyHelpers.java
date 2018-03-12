/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jopenvr;

import de.fruitfly.ovr.structs.Quatf;

public class JMonkeyHelpers
{
    // from JMonkeyEngine DQuaternion.java
    public static Quatf convertMatrix4ftoRotationQuat(float m00, float m01, float m02,
                                                      float m10, float m11, float m12, float m20, float m21, float m22) {
        // first normalize the forward (F), up (U) and side (S) vectors of the rotation matrix
        // so that the scale does not affect the rotation
        double lengthSquared = m00 * m00 + m10 * m10 + m20 * m20;
        if (lengthSquared != 1f && lengthSquared != 0f) {
            lengthSquared = 1.0 / Math.sqrt(lengthSquared);
            m00 *= lengthSquared;
            m10 *= lengthSquared;
            m20 *= lengthSquared;
        }
        lengthSquared = m01 * m01 + m11 * m11 + m21 * m21;
        if (lengthSquared != 1 && lengthSquared != 0f) {
            lengthSquared = 1.0 / Math.sqrt(lengthSquared);
            m01 *= lengthSquared;
            m11 *= lengthSquared;
            m21 *= lengthSquared;
        }
        lengthSquared = m02 * m02 + m12 * m12 + m22 * m22;
        if (lengthSquared != 1f && lengthSquared != 0f) {
            lengthSquared = 1.0 / Math.sqrt(lengthSquared);
            m02 *= lengthSquared;
            m12 *= lengthSquared;
            m22 *= lengthSquared;
        }

        // Use the Graphics Gems code, from
        // ftp://ftp.cis.upenn.edu/pub/graphics/shoemake/quatut.ps.Z
        // *NOT* the "Matrix and Quaternions FAQ", which has errors!

        // the trace is the sum of the diagonal elements; see
        // http://mathworld.wolfram.com/MatrixTrace.html
        float t = m00 + m11 + m22;

        // we protect the division by s by ensuring that s>=1
        Quatf quat = new Quatf();
        if (t >= 0) { // |w| >= .5
            double s = Math.sqrt(t + 1); // |s|>=1 ...
            quat.w = (float)(0.5f * s);
            s = 0.5f / s;                 // so this division isn't bad
            quat.x = (float)((m21 - m12) * s);
            quat.y = (float)((m02 - m20) * s);
            quat.z = (float)((m10 - m01) * s);
        } else if (m00 > m11 && m00 > m22) {
            double s = Math.sqrt(1.0 + m00 - m11 - m22); // |s|>=1
            quat.x = (float)(s * 0.5f); // |x| >= .5
            s = 0.5f / s;
            quat.y = (float)((m10 + m01) * s);
            quat.z = (float)((m02 + m20) * s);
            quat.w = (float)((m21 - m12) * s);
        } else if (m11 > m22) {
            double s = Math.sqrt(1.0 + m11 - m00 - m22); // |s|>=1
            quat.y = (float)(s * 0.5f); // |y| >= .5
            s = 0.5f / s;
            quat.x = (float)((m10 + m01) * s);
            quat.z = (float)((m21 + m12) * s);
            quat.w = (float)((m02 - m20) * s);
        } else {
            double s = Math.sqrt(1.0 + m22 - m00 - m11); // |s|>=1
            quat.z = (float)(s * 0.5f); // |z| >= .5
            s = 0.5f / s;
            quat.x = (float)((m02 + m20) * s);
            quat.y = (float)((m21 + m12) * s);
            quat.w = (float)((m10 - m01) * s);
        }

        return quat;
    }
}
