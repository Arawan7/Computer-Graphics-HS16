package assignment3;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.math.Quaternion;

public class MathHelper {
	public static Vector3f QuaternionVectorRotation( Quaternion quat, Vector3f vecO) {
		return QuaternionVectorRotation(vecO.x,vecO.y,vecO.z, quat.getX(),quat.getY(),quat.getZ(),quat.getW());
    }

	public static Vector3f QuaternionVectorRotation( Quat4d quat, Vector3f vecO) {
		return QuaternionVectorRotation(vecO.x,vecO.y,vecO.z, quat.x,quat.y,quat.z,quat.w);
    }

	public static Vector3f QuaternionVectorRotation( Quat4f quat, Vector3f vecO) {
		return QuaternionVectorRotation(vecO.x,vecO.y,vecO.z, quat.x,quat.y,quat.z,quat.w);
    }

	public static Vector3d QuaternionVectorRotation( double vecX, double vecY, double vecZ, double x,double y,double z,double w) {
		Vector3d vec = new Vector3d(vecX,vecY,vecZ);
        double sz = vec.length();
        vec.normalize();
        Quat4d quat = new Quat4d(x,y,z,w); //[x,y,z,w];
        Quat4d quatInv = new Quat4d(-x,-y,-z,w);
        Quat4d qVec = new Quat4d(vec.x, vec.y, vec.z, 0.0f);
        Quat4d temp = new Quat4d();
        temp.set(quat);
        temp.mul(qVec);
        temp.mul(quatInv);
		temp.normalize();
        Vector3d rotVec = new Vector3d((sz*temp.x),(sz*temp.y),(sz*temp.z));
        return rotVec;
    }
	public static Vector3f QuaternionVectorRotation( float vecX, float vecY, float vecZ, double x,double y,double z,double w) {
		Vector3d vec = new Vector3d(vecX,vecY,vecZ);
        double sz = vec.length();
        vec.normalize();
        Quat4d quat = new Quat4d(x,y,z,w); //[x,y,z,w];
        Quat4d quatInv = new Quat4d(-x,-y,-z,w);
        Quat4d qVec = new Quat4d(vec.x, vec.y, vec.z, 0.0f);
        Quat4d temp = new Quat4d();
        temp.set(quat);
        temp.mul(qVec);
        temp.mul(quatInv);
		temp.normalize();
        Vector3f rotVec = new Vector3f((float)(sz*temp.x),(float)(sz*temp.y),(float)(sz*temp.z));
		return rotVec;
    }
	
	public static float[] scalarMultiply(float[] array, float scale){
		float[] arr = new float[array.length];
		for (int i = 0;i<array.length;i++){
			arr[i]=array[i]*scale;
		}
		return arr;
	}
	
	public static Vector3f multiply(Matrix3f mat, Vector3f vector) {
        float x = mat.m00 * vector.x + mat.m01 * vector.y + mat.m02 * vector.z;
        float y = mat.m10 * vector.x + mat.m11 * vector.y + mat.m12 * vector.z;
        float z = mat.m20 * vector.x + mat.m21 * vector.y + mat.m22 * vector.z;
        return new Vector3f(x, y, z);
    }
	public static boolean vecEqual(Vector3f vec1, Vector3f vec2) {
		return vec1.x==vec2.x&&vec1.y==vec2.y&&vec1.z==vec2.z;
    }
	public static boolean vecEqualDir(Vector3f vec1, Vector3f vec2) {
		Vector3f temp1 = new Vector3f();temp1.set(vec1);temp1.normalize();
		Vector3f temp2 = new Vector3f();temp2.set(vec2);temp2.normalize();
		return temp1.x==temp2.x&&temp1.y==temp2.y&&temp1.z==temp2.z;
    }
	public static boolean vecEqualNegDir(Vector3f vec1, Vector3f vec2) {
		Vector3f temp1 = new Vector3f();temp1.set(vec1);temp1.normalize();
		Vector3f temp2 = new Vector3f();temp2.set(vec2);temp2.normalize();temp2.scale(-1.0f);
		return temp1.x==temp2.x&&temp1.y==temp2.y&&temp1.z==temp2.z;
    }
	public static boolean vecEqualNegDir(Vector3f vec1, Vector3f vec2, float eps) {
		Vector3f temp1 = new Vector3f();temp1.set(vec1);
		Vector3f temp2 = new Vector3f();temp2.set(vec2);
		return Math.abs(temp1.x+temp2.x)<eps&&
				Math.abs(temp1.y+temp2.y)<eps&&
				Math.abs(temp1.z+temp2.z)<eps;
    }
	public static boolean vecAbsEqualDir(Vector3f vec1, Vector3f vec2) {
		return vecEqualDir(vec1, vec2)||vecEqualNegDir(vec1, vec2);
    }

	public static Quat4f getQuatDiff( Vector3f vec1, Vector3f vec2) {
		Vector3f temp1 = new Vector3f(vec1);temp1.normalize();
		Vector3f temp2 = new Vector3f(vec2);temp2.normalize();
		Vector3f orth = new Vector3f();
		Quat4f quat = new Quat4f(0.0f,0.0f,0.0f,1.0f);
		if (vecEqualDir(temp1,temp2)){
			return quat;
		} else if (vecEqualNegDir(temp1,temp2)){
			quat.w = -1.0f;
			return quat;
		}
		orth.cross(temp1, temp2);orth.normalize();
		double angle2 =temp1.angle(temp2)/2.0;
		float cos2 = (float)Math.cos(angle2);
		orth.scale((float)Math.sin(angle2));
		quat.set(orth.x,orth.y,orth.z,cos2);
		quat.normalize();
		return quat;
    }
}
