#Controls back and front faces and drives slave for left and right
import socket
import const
import brickpi
import time

#Initialise brickpi
interface=brickpi.Interface()
interface.initialize()
print("finished init")

#Initialise motors
backClampMotor = 3
backRotMotor = 2
frontClampMotor = 1
frontRotMotor = 0

def wait(motors):
	prev = [tups[0] for tups in interface.getMotorAngles(motors)]
	while not interface.motorAngleReferencesReached(motors):
		time.sleep(0.1) 
		angles = [tups[0] for tups in interface.getMotorAngles(motors)]
		if(angles == prev):
			break
		else:
			prev = angles		
def clampFront():
	interface.increaseMotorAngleReferences([frontClampMotor],[-const.clamp])
	wait([frontClampMotor])

def clampBack():
	interface.increaseMotorAngleReferences([backClampMotor],[-const.clamp])
	wait([backClampMotor])

def clampBoth():
	interface.increaseMotorAngleReferences([backClampMotor, frontClampMotor],[-const.clamp,-const.clamp])
	wait([backClampMotor,frontClampMotor])
	 
def releaseBoth():
	interface.increaseMotorAngleReferences([backClampMotor,frontClampMotor],[const.clamp,const.clamp])
	wait([backClampMotor,frontClampMotor])

def releaseFront():
	interface.increaseMotorAngleReferences([frontClampMotor],[const.clamp])
	wait([frontClampMotor])

def releaseBack():
	interface.increaseMotorAngleReferences([backClampMotor],[const.clamp])
	wait([backClampMotor])

def turnFrontClockwise():
	interface.increaseMotorAngleReferences([frontRotMotor],[const.quarterTurn])
	wait([frontRotMotor])
	releaseFront()	
	interface.increaseMotorAngleReferences([frontRotMotor],[-const.quarterTurn-0.02])
	wait([frontRotMotor])
	clampFront()

interface.motorEnable(backClampMotor)
interface.motorEnable(backRotMotor)
interface.motorEnable(frontClampMotor)
interface.motorEnable(frontRotMotor)
print("Motors enabled")

rotMotorParams = interface.MotorAngleControllerParameters()
rotMotorParams.maxRotationAcceleration = 20.0
rotMotorParams.maxRotationSpeed = 100.0
rotMotorParams.feedForwardGain = 255/20.0
rotMotorParams.minPWN = 18.0
rotMotorParams.pidParameters.minOutput = -255
rotMotorParams.pidParameters.maxOutput = 255
rotMotorParams.pidParameters.k_p = 600.0
rotMotorParams.pidParameters.k_i = 0.0
rotMotorParams.pidParameters.k_d = 0.0
interface.setMotorAngleControllerParameters(backRotMotor, rotMotorParams)
interface.setMotorAngleControllerParameters(frontRotMotor, rotMotorParams)

clampMotorParams = interface.MotorAngleControllerParameters()
clampMotorParams.maxRotationAcceleration = 20.0
clampMotorParams.maxRotationSpeed = 180.0
clampMotorParams.feedForwardGain = 255/20.0
clampMotorParams.minPWM = 20.0
clampMotorParams.pidParameters.minOutput = -255
clampMotorParams.pidParameters.maxOutput = 255
clampMotorParams.pidParameters.k_p = 400.0
clampMotorParams.pidParameters.k_i = 0.0
clampMotorParams.pidParameters.k_d = 0.0
interface.setMotorAngleControllerParameters(backClampMotor, clampMotorParams)
interface.setMotorAngleControllerParameters(frontClampMotor, clampMotorParams)

def clampAll(socket):
	socket.sendall(b'clampBoth')
	clampBoth()
	conn.recv(1024)

def releaseAll(socket):
	socket.sendall(b'releaseBoth')
	releaseBoth()
	socket.recv(1024)

host = ''
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host,port))
s.listen(1)

moveToFunctionDict = {'F':turnFrontClockwise}


conn, addr = s.accept()
print('Connected by', addr)
clampAll(conn)

while True:
	command = raw_input("Enter move:")
	
	moveToFunctionDict[command]()
	
conn.close()




