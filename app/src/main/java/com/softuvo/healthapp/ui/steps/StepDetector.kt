package com.softuvo.healthapp.ui.steps

import com.softuvo.healthapp.data.interfaces.StepListener

class StepDetector
{
    private var ACCEL_RING_SIZE = 50
    private var VEL_RING_SIZE = 10

    // change this threshold according to your sensitivity preferences
    private var STEP_THRESHOLD = 40f

    private var STEP_DELAY_NS = 25000000

    private var accelRingCounter = 0

    private var accelRingX = FloatArray(ACCEL_RING_SIZE)
    private var accelRingY = FloatArray(ACCEL_RING_SIZE)
    private var accelRingZ = FloatArray(ACCEL_RING_SIZE)
    private var velRingCounter = 0
    private var velRing = FloatArray(VEL_RING_SIZE)
    private var lastStepTimeNs: Long = 0
    private var oldVelocityEstimate = 0f

    private var listener: StepListener? = null
    private var filter:SensorFilter= SensorFilter()
    fun registerListener(listener: StepListener) {
        this.listener = listener
    }


    fun updateAccel(timeNs: Long, x: Float, y: Float, z: Float) {
        val currentAccel = FloatArray(3)
        currentAccel[0] = x
        currentAccel[1] = y
        currentAccel[2] = z

        // First step is to update our guess of where the global z vector is.
        accelRingCounter++
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0]
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1]
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2]

        var worldZ = FloatArray(3)
        worldZ[0] = filter.sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE)
        worldZ[1] = filter.sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE)
        worldZ[2] = filter.sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE)
        var normalization_factor = filter.norm(worldZ)

        worldZ[0] = worldZ[0] / normalization_factor
        worldZ[1] = worldZ[1] / normalization_factor
        worldZ[2] = worldZ[2] / normalization_factor

        var currentZ = filter.dot(worldZ, currentAccel) - normalization_factor
        velRingCounter++
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ

        var velocityEstimate = filter.sum(velRing)

        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
            && timeNs - lastStepTimeNs > STEP_DELAY_NS
        ) {
            listener!!.step(timeNs)
            lastStepTimeNs = timeNs
        }

        oldVelocityEstimate = velocityEstimate
    }
}