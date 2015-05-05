#In charge of R and L faces
import brickpi
import socket
import time
import const
import localMotor

host = '192.168.0.11'
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((host,port))	

#Initialise brickpi
interface=brickpi.Interface()
interface.initialize()

#Initialise motors
rightClampMotor = 1
rightRotMotor = 0
leftClampMotor = 2
leftRotMotor = 3

interface.motorEnable(rightClampMotor)
interface.motorEnable(rightRotMotor)
interface.motorEnable(leftClampMotor)
interface.motorEnable(leftRotMotor)

rotMotorParams = const.getRotationParams(interface)
interface.setMotorAngleControllerParameters(rightRotMotor, rotMotorParams)
interface.setMotorAngleControllerParameters(leftRotMotor, rotMotorParams)

clampMotorParams = const.getClampParams(interface)
interface.setMotorAngleControllerParameters(rightClampMotor, clampMotorParams)
interface.setMotorAngleControllerParameters(leftClampMotor, clampMotorParams)
print "Motors Enabled"
	
def clampBoth():
	print "clamping"
	localMotor.clamp([leftClampMotor,rightClampMotor], interface)	

def clampRight():
	localMotor.clamp([rightClampMotor], interface)
	
def clampLeft():
	localMotor.clamp([leftClampMotor], interface)

def releaseBoth():
	localMotor.release([leftClampMotor,rightClampMotor], interface)

def receiveCommand(command):
	command()

def halfClampRight():
	localMotor.halfClamp([rightClampMotor], interface)

def halfClampLeft():
	localMotor.halfClamp([leftClampMotor], interface)

def halfReleaseRight():
	localMotor.halfRelease([rightClampMotor], interface)

def halfReleaseLeft():
	localMotor.halfRelease([leftClampMotor], interface)

def turnRightClockwise():
	print "Turning right"
	localMotor.turnClockwise([rightRotMotor], halfClampRight, halfReleaseRight, interface)

def turnLeftClockwise():
	print "Turning left"
	localMotor.turnClockwise([leftRotMotor], halfClampLeft, halfReleaseLeft, interface)	

def turnRightAnti():
	print "Turning right Anti"
	localMotor.turnAnti([rightRotMotor], halfClampRight, halfReleaseRight, interface)

def turnLeftAnti():
	print "Turning left Anti"
	localMotor.turnAnti([leftRotMotor], halfClampLeft, halfReleaseLeft, interface)	

while True:
	data = s.recv(1024)
	if not data : continue
	print "Got command"
	if(data == 'quit'): break
	receiveCommand(eval(data))
	s.sendall(b'done')
s.close
