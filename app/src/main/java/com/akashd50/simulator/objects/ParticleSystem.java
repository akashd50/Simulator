package com.akashd50.simulator.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ParticleSystem {
    private static long GLOBAL_TIME;
    public static final int LIGHT_BLEND = 1;
    public static final int VN_BLEND = 2;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int ACC_COMPONENT_COUNT = 3;

    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
                    + COLOR_COMPONENT_COUNT
                    + VECTOR_COMPONENT_COUNT
                    + PARTICLE_START_TIME_COMPONENT_COUNT;

    private int uMatrixLocation,uTimeLocation, aPositionLocation, aColorLocation,
            aDirectionVectorLocation,aParticleStartTimeLocation, mProgram, uTextureLocation, aAccLocation, texture;

    private FloatBuffer positionBuffer, colorBuffer, vectorBuffer, timeBuffer, accBuffer;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;
    private int resID, particleCount, BLEND_TYPE,currentParticleCount, nextParticle;
    private float timeOnScreen, pointer;
    private float[] positions, colors, velocity, times, acceleration;
    private long timeLastParticleAdded, timeCurrent;
    private ShaderProgram shaderProgram;

    public ParticleSystem(int count) {
        GLOBAL_TIME = System.nanoTime();
        this.particleCount = count;

        positions = new float[particleCount*POSITION_COMPONENT_COUNT];
        colors = new float[particleCount*COLOR_COMPONENT_COUNT];
        velocity = new float[particleCount*VECTOR_COMPONENT_COUNT];
        times = new float[particleCount*PARTICLE_START_TIME_COMPONENT_COUNT];
        acceleration = new float[particleCount*ACC_COMPONENT_COUNT];

        BLEND_TYPE = LIGHT_BLEND;
        timeLastParticleAdded = 0;
        timeCurrent = 0;
        this.timeOnScreen = 3.0f;
        pointer = 10f;
        currentParticleCount = 0;
        nextParticle = 0;

        ByteBuffer bb = ByteBuffer.allocateDirect(positions.length * BYTES_PER_FLOAT);
        if(bb!=null) {
            bb.order(ByteOrder.nativeOrder());
            positionBuffer = bb.asFloatBuffer();
            positionBuffer.put(positions);
            positionBuffer.position(0);
        }
        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length*BYTES_PER_FLOAT);
        if(cb!=null){
            cb.order(ByteOrder.nativeOrder());
            colorBuffer = cb.asFloatBuffer();
            colorBuffer.put(colors);
            colorBuffer.position(0);
        }

        ByteBuffer vb = ByteBuffer.allocateDirect(velocity.length*BYTES_PER_FLOAT);
        if(vb!=null){
            vb.order(ByteOrder.nativeOrder());
            vectorBuffer = vb.asFloatBuffer();
            vectorBuffer.put(velocity);
            vectorBuffer.position(0);
        }

        ByteBuffer tb = ByteBuffer.allocateDirect(times.length*BYTES_PER_FLOAT);
        if(tb!=null){
            tb.order(ByteOrder.nativeOrder());
            timeBuffer = tb.asFloatBuffer();
            timeBuffer.put(times);
            timeBuffer.position(0);
        }

        ByteBuffer ab = ByteBuffer.allocateDirect(acceleration.length*BYTES_PER_FLOAT);
        if(ab!=null){
            ab.order(ByteOrder.nativeOrder());
            accBuffer = ab.asFloatBuffer();
            accBuffer.put(acceleration);
            accBuffer.position(0);
        }
    }

    public void addParticlesCircle(int num, int color, float rad, Vector location, Vector dir, Vector flowDir){
        for(int i=0;i<num;i++) {
            if (flowDir.x == 1f) {

            } else if (flowDir.y == 1f) {
                float rand = (float) Math.random() * rad;
                float rand2 = (float) Math.random() * rad;
                Vector tLoc = new Vector();
                tLoc.y = location.y;

                int r1 = (int) (Math.random() * 100);
                int r2 = (int) (Math.random() * 100);

                if (r1 % 2 == 0) {
                    tLoc.x = location.x + rand;
                } else {
                    tLoc.x = location.x - rand;
                }
                if (r2 % 2 == 0) {
                    tLoc.z = location.z + rand2;
                } else {
                    tLoc.z = location.z - rand2;
                }
                addParticle(tLoc, color, new Vector(dir.x, dir.y * (float) Math.random(), dir.z), null);
            } else if (flowDir.z == 1f) {

            }
        }
    }

    public void addParticles(int numP, int color, Vector pos, Vector posVar, Vector dir, Vector dirVar){
        for(int i=0;i<numP;i++){
            pos.x = pos.x +   (posVar.x)/2 - (float)(Math.random()*posVar.x);
            pos.y = pos.y + (posVar.y)/2 - (float)(Math.random()*posVar.y);
            pos.z = pos.z + (posVar.z)/2 - (float)(Math.random()*posVar.z);

            dir.x  = dir.x * (float)(Math.random());
            dir.y  = dir.y * (float)(Math.random());
            dir.z  = dir.z * (float)(Math.random());

            if(dirVar.x==1f){
                int rand = (int)(Math.random()*100);
                if(rand%2==0) dir.x = -dir.x;
            }
            if(dirVar.y==1f){
                int rand = (int)(Math.random()*100);
                if(rand%2==0) dir.y= -dir.y;
            }
            if(dirVar.z==1f){
                int rand = (int)(Math.random()*100);
                if(rand%2==0) dir.z= -dir.z;
            }
            addParticle(pos,color, dir, null);
        }
    }

    public void addParticle(Vector position, int color, Vector direction, Vector acc) {
        float particleStartTime = (System.nanoTime() - GLOBAL_TIME)/1000000000f;
        final int particleOffset = nextParticle * POSITION_COMPONENT_COUNT;
        int currentOffset = particleOffset;

        final int fcoff = nextParticle*COLOR_COMPONENT_COUNT;
        int colorOffset = fcoff;

        final int fvoff = nextParticle*VECTOR_COMPONENT_COUNT;
        int vectorOffset = fvoff;

        int accOffset = fvoff;

        final int ftoff = nextParticle* PARTICLE_START_TIME_COMPONENT_COUNT;
        int timeOffset = ftoff;


        nextParticle++;
        if (currentParticleCount < particleCount) {
            currentParticleCount++;
        }
      //  System.out.println("Adding particle");
        if (nextParticle == particleCount) {
       //     System.out.println("Set to Zero");
            nextParticle = 0;
        }

        positions[currentOffset++] = position.x;
        positions[currentOffset++] = position.y;
        positions[currentOffset++] = position.z;

        positionBuffer.position(particleOffset);
        positionBuffer.put(positions, particleOffset, POSITION_COMPONENT_COUNT);
        positionBuffer.position(0);

        colors[colorOffset++] = Color.red(color) / 255f;
        colors[colorOffset++] = Color.green(color) / 255f;
        colors[colorOffset++] = Color.blue(color) / 255f;

        colorBuffer.position(fcoff);
        colorBuffer.put(colors, fcoff, COLOR_COMPONENT_COUNT);
        colorBuffer.position(0);

        velocity[vectorOffset++] = direction.x;
        velocity[vectorOffset++] = direction.y;
        velocity[vectorOffset++] = direction.z;
        vectorBuffer.position(fvoff);
        vectorBuffer.put(velocity, fvoff, VECTOR_COMPONENT_COUNT);
        vectorBuffer.position(0);

        acceleration[accOffset++] = acc.x;
        acceleration[accOffset++] = acc.y;
        acceleration[accOffset++] = acc.z;
        accBuffer.position(fvoff);
        accBuffer.put(acceleration, fvoff, ACC_COMPONENT_COUNT);
        accBuffer.position(0);

        times[timeOffset++] = particleStartTime;
        timeBuffer.position(ftoff);
        timeBuffer.put(times, ftoff, PARTICLE_START_TIME_COMPONENT_COUNT);
        timeBuffer.position(0);

        timeLastParticleAdded = System.nanoTime();
    }

    public void update(TypePolygon o){
        float uTime = (System.nanoTime()-GLOBAL_TIME)/1000000000f;

        for(int i=0;i<currentParticleCount;i+=3){
            float elapsedTime2 = uTime - times[i];

            int pIndex = i*POSITION_COMPONENT_COUNT;
            //float x = positions[i] + (velocity[i]*elapsedTime2);
            //float y = positions[i+1] + (velocity[i+1]*elapsedTime2);
            //float z = positions[i+2] + (velocity[i+2]*elapsedTime2);



            //acceleration[pIndex+1] -= acceleration[pIndex+1]*0.2f;
            Vector reflection = null;
            if((reflection = o.pointTest(positions[pIndex],positions[pIndex+1],velocity[pIndex], velocity[pIndex+1])) != null){
            //if(o.lineTest(positions[pIndex],positions[pIndex+1], velocity[pIndex], velocity[pIndex+1])){
            //if(o.pointInPolygon(positions[pIndex],positions[pIndex+1], velocity[pIndex], velocity[pIndex+1])){
                //velocity[i] += velocity[i+1];

                //Vector v = o.reflect(positions[pIndex],positions[pIndex+1], velocity[pIndex], velocity[pIndex+1]);
                if(reflection!=null) {
                    velocity[pIndex] = reflection.x*0.8f;
                    velocity[pIndex + 1] = reflection.y*0.8f;

                    vectorBuffer.position(pIndex);
                    vectorBuffer.put(velocity, pIndex, VECTOR_COMPONENT_COUNT);
                    vectorBuffer.position(0);

                    positions[pIndex] += velocity[pIndex];
                    positions[pIndex + 1] += velocity[pIndex + 1];
                    positions[pIndex + 2] += velocity[pIndex + 2];
                }
                //System.out.println("Collision detected");
            }else{
                positions[pIndex] += velocity[pIndex];
                positions[pIndex+1] += velocity[pIndex+1];
                positions[pIndex+2] += velocity[pIndex+2];

                velocity[pIndex] += acceleration[pIndex];
                velocity[pIndex+1] += acceleration[pIndex+1];
            }

            positionBuffer.position(pIndex);
            positionBuffer.put(positions, pIndex, POSITION_COMPONENT_COUNT);
            positionBuffer.position(0);
        }


    }

    public void onDrawFrame(float[] mMVPMatrix){
        timeCurrent = System.nanoTime();
        float elapsedTime = (timeCurrent-GLOBAL_TIME)/1000000000f;
        //if((timeCurrent - timeLastParticleAdded)/1000000000 < this.timeOnScreen && timeLastParticleAdded!=0) {
            GLES30.glUseProgram(mProgram);
            uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
            GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix, 0);

            uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");
            GLES30.glUniform1f(uTimeLocation, elapsedTime);

            uTextureLocation = GLES30.glGetUniformLocation(mProgram, "u_TextureUnit");
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
            GLES30.glUniform1i(uTextureLocation, 0);

            aPositionLocation = GLES30.glGetAttribLocation(mProgram, "a_Position");
            aColorLocation = GLES30.glGetAttribLocation(mProgram, "a_Color");
            aDirectionVectorLocation = GLES30.glGetAttribLocation(mProgram, "a_DirectionVector");
            aParticleStartTimeLocation = GLES30.glGetAttribLocation(mProgram, "a_ParticleStartTime");


            positionBuffer.position(0);
            GLES30.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT, positionBuffer);
            GLES30.glEnableVertexAttribArray(aPositionLocation);


            colorBuffer.position(0);
            GLES30.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    COLOR_COMPONENT_COUNT * BYTES_PER_FLOAT, colorBuffer);
            GLES30.glEnableVertexAttribArray(aColorLocation);

            vectorBuffer.position(0);
            GLES30.glVertexAttribPointer(aDirectionVectorLocation, VECTOR_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    VECTOR_COMPONENT_COUNT * BYTES_PER_FLOAT, vectorBuffer);
            GLES30.glEnableVertexAttribArray(aDirectionVectorLocation);


            accBuffer.position(0);
            GLES30.glVertexAttribPointer(aAccLocation, ACC_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    ACC_COMPONENT_COUNT * BYTES_PER_FLOAT, accBuffer);
            GLES30.glEnableVertexAttribArray(aAccLocation);

            timeBuffer.position(0);
            GLES30.glVertexAttribPointer(aParticleStartTimeLocation, PARTICLE_START_TIME_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    PARTICLE_START_TIME_COMPONENT_COUNT * BYTES_PER_FLOAT, timeBuffer);
            GLES30.glEnableVertexAttribArray(aParticleStartTimeLocation);

            GLES30.glDisable(GLES30.GL_CULL_FACE);

            GLES30.glEnable(GLES20.GL_BLEND);
       //     if (BLEND_TYPE == VN_BLEND) {
                //GLES30.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
         //   } else if (BLEND_TYPE == LIGHT_BLEND) {
                GLES30.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
            //} else {
              //  GLES30.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
         //   }

            GLES30.glDrawArrays(GLES30.GL_POINTS, 0, currentParticleCount);

            //disable the locations
            GLES30.glDisableVertexAttribArray(aPositionLocation);
            GLES30.glDisableVertexAttribArray(aDirectionVectorLocation);
            GLES30.glDisableVertexAttribArray(aColorLocation);
            GLES30.glDisableVertexAttribArray(aParticleStartTimeLocation);
            //GLES30.glDisable(GLES20.GL_BLEND);
       // }
    }

    public void setBlendType(int blend){
        this.BLEND_TYPE = blend;
    }
    public void setPointerSize(float p){
        this.pointer = p;
    }
    public void setTimeOnScreen(float time){
        this.timeOnScreen = time;
    }

    public void setTexture(Texture texture){
        this.texture = texture.getTexture();
    }

    public void loadTexture(Context context, int resID){
        this.resID = resID;
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), this.resID, options);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        this.texture = textures[0];
    }

    public void setProgram(ShaderProgram shaderProgram){
        this.shaderProgram = shaderProgram;
        this.mProgram = shaderProgram.getProgram();

        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");
        aPositionLocation = GLES30.glGetAttribLocation(mProgram, "a_Position");
        aColorLocation = GLES30.glGetAttribLocation(mProgram, "a_Color");
        aDirectionVectorLocation = GLES30.glGetAttribLocation(mProgram, "a_DirectionVector");
        aAccLocation = GLES30.glGetAttribLocation(mProgram,"a_accVector");
        aParticleStartTimeLocation = GLES30.glGetAttribLocation(mProgram, "a_ParticleStartTime");
        uTextureLocation = GLES30.glGetUniformLocation(mProgram, "u_TextureUnit");
    }
}
