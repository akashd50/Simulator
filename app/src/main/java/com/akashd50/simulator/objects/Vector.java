package com.akashd50.simulator.objects;

public class Vector {
    public float x, y,z;

    public Vector(){

    }

    public Vector(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static float dot(Vector v1, Vector v2){
        return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
    }

    public Vector getVectorTo(Vector v2){
        return new Vector(v2.x-x, v2.y-y,v2.z-z);
    }

    /*public Vector cross(Vector v2, Vector toMult){
        float ma = this.magnitude(v2);
        float mb = toMult.magnitude();
        Vector temp = new Vector();
        float angle = (float)Math.tanh((toMult.y/(v2.x - this.x)));
        temp.x = (float)(ma * mb * Math.sin(angle) * 1f);
        temp.y = (float)(ma * mb * Math.sin(angle) * 1f);
        temp.x = (float)(ma * mb * Math.sin(angle) * 1f);

        return temp;
    }*/

    public Vector reflect(Vector v2, Vector toMult){
        Vector temp = new Vector();
        Vector normal = this.normal(v2,true);
        temp.x = (float)(toMult.x - 2*(dot(toMult, normal))*normal.x);
        temp.y = (float)(toMult.y - 2*(dot(toMult, normal))*normal.y);
        temp.z = (float)(toMult.z - 2*(dot(toMult, normal))*normal.z);

        return temp;

    }

    public Vector normal(Vector v2, boolean up){
        Vector temp = new Vector();

        if(up) {
            temp.y = (v2.x - this.x);
            temp.x = -(v2.y - this.y);
        }
        else {
            temp.y = -(v2.x - this.x);
            temp.x = (v2.y - this.y);
        }

        temp.z = v2.z - this.z;
        return temp.unit(temp.magnitude());
    }

    public float magnitude(Vector v2){
        return (float)(Math.sqrt(Math.pow(v2.x-x,2) + Math.pow(v2.y-y,2) + Math.pow(v2.z-z,2)));
    }

    public float magnitude(){
        return (float)(Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2)));
    }

    public Vector unit(float mag){
        Vector temp = new Vector();
        temp.x = this.x/mag;
        temp.y = this.y/mag;
        temp.z = this.z/mag;
        return temp;
    }

    public static Vector minX(Vector s1, Vector s2, Vector s3){
        if(s1.x<s2.x){
            if(s1.x<s3.x){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.x<s3.x){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static Vector minY(Vector s1, Vector s2, Vector s3){
        if(s1.y<s2.y){
            if(s1.y<s3.y){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.y<s3.y){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static Vector maxX(Vector s1, Vector s2, Vector s3){
        if(s1.x>s2.x){
            if(s1.x>s3.x){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.x>s3.x){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static Vector maxY(Vector s1, Vector s2, Vector s3){
        if(s1.y>s2.y){
            if(s1.y>s3.y){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.y>s3.y){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static Vector minZ(Vector s1, Vector s2, Vector s3){
        if(s1.z<s2.z){
            if(s1.z<s3.z){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.z<s3.z){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static Vector maxZ(Vector s1, Vector s2, Vector s3){
        if(s1.z>s2.z){
            if(s1.z>s3.z){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.z>s3.z){
                return s2;
            }else{
                return s3;
            }
        }
    }

    public float subtract(Vector s){
        float x = s.x - this.x;
        float y = s.y - this.y;
        float z = s.z = this.z;
        float difference = (float) Math.sqrt(x*x + y*y + z*z);
        return difference;
    }

    public String toString(){
        return "("+x+", "+y+", "+z+")";
    }
}
