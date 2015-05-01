import time
import socket
import threading
import brickpi

#Initialise brickpi
interface=brickpi.Interface()
interface.initialize()

#Initialise motors
rightClamp = 0
rightRot = 1
left 

motors = [0,1]
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

#Set motor reference points
ninetyDeg = 2.4
clamp = interface.getMotorAngles(motors)
unclamp = 1

#A ninety degree turn
interface.increaseMotorAngleReferences(motors,[ninetyDeg])

interface.terminate()

masterIp = '192.168.0.26'
masterPort = 12345

masterSocket = connectToMaster()
	

#Send instruction
def connectToMaster(instr,ip,port):
	s = socket.socket()
	s.connect((ip, port))
	return s

#Boolean that tracks the state of the clamping claw
isClamped = false

def clamp():

def unclamp():


def waitForCommand():
	#block thread until we get something
	instructions = masterSocket.recv(1024)
		
	
