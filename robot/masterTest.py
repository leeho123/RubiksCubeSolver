#Controls back and front faces and drives slave for left and right
import socket
import const
import brickpi
import time
import localMotor
import slaveMotor

#Initialise brickpi
interface=brickpi.Interface()
interface.initialize()
print("finished init")

#Initialise motors
backClampMotor = 2
backRotMotor = 3
frontClampMotor = 1
frontRotMotor = 0

def clampFront():
	localMotor.clamp([frontClampMotor],interface)

def clampBack():
	localMotor.clamp([backClampMotor],interface)

def clampBoth():
	localMotor.clamp([frontClampMotor, backClampMotor],interface)	

def releaseBoth():
	localMotor.release([frontClampMotor, backClampMotor],interface)	

def releaseFront():
	localMotor.release([frontClampMotor],interface)

def releaseBack():
	localMotor.release([backClampMotor],interface)

def halfClampFront():
	localMotor.halfClamp([frontClampMotor], interface)
	
def halfReleaseFront():
	localMotor.halfRelease([frontClampMotor], interface)

def halfClampBack():
	localMotor.halfClamp([backClampMotor], interface)

def halfReleaseBack():
	localMotor.halfRelease([backClampMotor], interface)        

def turnFrontClockwise():
	localMotor.turnClockwise([frontRotMotor], halfClampFront, halfReleaseFront, interface)

def turnBackClockwise():
	localMotor.turnClockwise([backRotMotor], halfClampBack, halfReleaseBack, interface)

interface.motorEnable(backClampMotor)
interface.motorEnable(backRotMotor)
interface.motorEnable(frontClampMotor)
interface.motorEnable(frontRotMotor)
print("Motors enabled")

rotMotorParams = const.getRotationParams(interface)
interface.setMotorAngleControllerParameters(backRotMotor, rotMotorParams)
interface.setMotorAngleControllerParameters(frontRotMotor, rotMotorParams)

clampMotorParams = const.getClampParams(interface)
interface.setMotorAngleControllerParameters(backClampMotor, clampMotorParams)
interface.setMotorAngleControllerParameters(frontClampMotor, clampMotorParams)

def clampAll(socket):
	slaveMotor.clampBoth(socket, clampBoth)

def releaseAll(socket):
	slaveMotor.releaseBoth(socket, releaseBoth)

host = ''
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host,port))
s.listen(1)

def turnRightClockwise():
        slaveMotor.turnRightClockwise(s)

def turnLeftClockwise():
        slaveMotor.turnLeftClockwise(s)


moveToFunctionDict = {'F':turnFrontClockwise, 'B':turnBackClockwise, 
			'R':turnRightClockwise, 'L':turnLeftClockwise}

conn, addr = s.accept()
print('Connected by', addr)
clampAll(conn)

while True:
	command = raw_input("Enter move:")
	if command in moveToFunctionDict:	
		moveToFunctionDict[command]()
	if command == "quit":
		releaseAll(conn)
		conn.sendall(b'quit')
		break
	
conn.close()




