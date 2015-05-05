import brickpi
import time

interface=brickpi.Interface()
interface.initialize()

motors = [1]

interface.motorEnable(motors[0])

motorParams = interface.MotorAngleControllerParameters()
motorParams.maxRotationAcceleration = 20.0
motorParams.maxRotationSpeed = 100.0
motorParams.feedForwardGain = 255/20.0
motorParams.minPWM = 18.0
motorParams.pidParameters.minOutput = -255
motorParams.pidParameters.maxOutput = 255
motorParams.pidParameters.k_p = 600.0
motorParams.pidParameters.k_i = 0.0
motorParams.pidParameters.k_d = 0.0

interface.setMotorAngleControllerParameters(motors[0],motorParams)

while True:
	angle = float(input("Enter a angle to rotate (in radians): "))
	interface.increaseMotorAngleReferences(motors,[angle])

	while not interface.motorAngleReferencesReached(motors) :
		motorAngles = interface.getMotorAngles(motors)
		if motorAngles :
			print "Motor angles: ", motorAngles[0][0]
		time.sleep(0.1)

	print "Destination reached!"
	

interface.terminate()
