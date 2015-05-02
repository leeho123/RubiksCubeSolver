#In charge of R and L faces
import brickpi
import socket
import time
import const

host = '192.168.0.33'
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((host,port))	

#Initialise brickpi
interface=brickpi.Interface()
interface.initialize()

#Initialise motors
rightClampMotor = 1
rightRotMotor = 0
leftClampMotor = 3
leftRotMotor = 2

interface.motorEnable(rightClampMotor)
interface.motorEnable(rightRotMotor)
interface.motorEnable(leftClampMotor)
interface.motorEnable(leftRotMotor)

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
interface.setMotorAngleControllerParameters(rightRotMotor, rotMotorParams)
interface.setMotorAngleControllerParameters(leftRotMotor, rotMotorParams)

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
interface.setMotorAngleControllerParameters(rightClampMotor, clampMotorParams)
interface.setMotorAngleControllerParameters(leftClampMotor, clampMotorParams)
	
def wait(motors):
	prev = [tups[0] for tups in interface.getMotorAngles(motors)]
	while not interface.motorAngleReferencesReached(motors):
		time.sleep(0.1)
		angles = [tups[0] for tups in interface.getMotorAngles(motors)]
		if (angles == prev):
			break
		else:
			prev = angles
def clampBoth():
	interface.increaseMotorAngleReferences([rightClampMotor,leftClampMotor], [-const.clamp,-const.clamp])	
	wait([rightClampMotor, leftClampMotor])

def clampRight():
	interface.increaseMotorAngleReferences([rightClampMotor],[-const.clamp])	
	wait([rightClampMotor])
	
def clampLeft():
	interface.increaseMotorAngleReferences([leftClampMotor],[-const.clamp])	
	wait([leftClampMotor])

commandMap = {'clampBoth': clampBoth}

def releaseBoth():
	interface.increaseMotorAngleReferences([rightClampMotor,leftClampMotor],[const.clamp,const.clamp])
	wait([rightClampMotor,leftClampMotor])

def receiveCommand(command):
	command()
		
while True:
	data = s.recv(1024)
	if not data : continue
	receiveCommand(eval(data))
	s.sendall(b'done')
s.close
