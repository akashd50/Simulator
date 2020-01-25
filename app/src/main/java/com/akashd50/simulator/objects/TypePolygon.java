package com.akashd50.simulator.objects;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.util.ArrayList;

public class TypePolygon {
    private Vector position;
    private ArrayList<Vector> vertices;
    private Quad2D quad;
    public TypePolygon(Vector position, Vector dimens){
        this.position = position;
        vertices = new ArrayList<>();
        vertices.add(new Vector(position.x-dimens.x,position.y+dimens.y,position.z));
        vertices.add(new Vector(position.x-dimens.x,position.y-dimens.y,position.z));
        vertices.add(new Vector(position.x+dimens.x,position.y-dimens.y,position.z));
        vertices.add(new Vector(position.x+dimens.x,position.y+dimens.y,position.z));
    }

    public TypePolygon(){
        vertices = new ArrayList<>();
    }

    public void addVertex(Vector vector){
        vertices.add(vector);
    }

    public Vector pointTest(float x, float y, float vx, float vy){
        float radius = 0.02f;
        for(int i=0;i<vertices.size();i++){
            Vector v1 = vertices.get(i);
            Vector v2 = null;

            if(i+1<vertices.size()) v2 = vertices.get(i+1);
            else v2 = vertices.get(0);

            if(checkIntersection(x,y+radius,x,y-radius, v1.x,v1.y, v2.x,v2.y) ||
                    checkIntersection(x-radius,y,x+radius,y, v1.x,v1.y, v2.x,v2.y)){
                //return reflect(x,y,vx,vy);
//            if(checkIntersection(x,y+radius,x+radius,y, v1.x,v1.y, v2.x,v2.y) ||
//                    checkIntersection(x+radius,y,x,y-radius, v1.x,v1.y, v2.x,v2.y) ||
//                    checkIntersection(x,y-radius,x-radius,y, v1.x,v1.y, v2.x,v2.y) ||
//                    checkIntersection(x-radius,y,x,y+radius, v1.x,v1.y, v2.x,v2.y)){

                return v1.reflect(v2,new Vector(vx,vy,0f));
            }
        }
        return null;
    }

    public boolean pointInPolygon(float x, float y, float vx, float vy){
        int totalCrossings=0;
        for(int i=0;i<vertices.size();i++){
            Vector v1 = vertices.get(i);
            Vector v2 = null;

            if(i+1<vertices.size()) v2 = vertices.get(i+1);
            else v2 = vertices.get(0);

            if(checkIntersection(x,y,x+10,y, v1.x,v1.y, v2.x,v2.y)) totalCrossings++;
        }

        if(totalCrossings%2!=0) return true;
        else return false;
    }

    public boolean lineTest(float x, float y, float vx, float vy){
        float left = position.x - 0.5f;
        float right = position.x + 0.5f;
        float top = position.y + 0.1f;
        float bottom = position.y - 0.1f;
        float radius = 0.05f;
        float lineTX = x- (vx*(radius/-vx));
        float lineTY = y- (vy*(radius/-vy));

        float lineBX = x+ (vx*(radius/-vx));
        float lineBY = y+ (vy*(radius/-vy));

        if(checkIntersection(lineTX,lineTY,lineBX,lineBY, left,top, right,top)) return true;

        //if(x<right && x>left && y<top && y>bottom) return true;

        return false;
    }

    public Vector reflect(float px, float py, float vx, float vy){
        if(py>position.y){
            return vertices.get(0).reflect(vertices.get(3),new Vector(vx,vy,0f));
        }else if(py<position.y){

        }
        return null;
    }


    public boolean checkIntersection(float x1, float y1, float x2,float y2, float x3, float y3, float x4, float y4){
        float den = (y4-y3)*(x2-x1) - (x4-x3)*(y2-y1);
        if (den == 0.0f) {
            return false;
        }
        float ta = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3))/den;
        float tb = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3))/den;
        if(ta>=0 && ta<= 1f && tb>=0 && tb<=1f) return true;
        else return false;
    }

}
