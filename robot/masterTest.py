import socket
import const
import brickpi

#Initialise brickpi
interface=brickpi.Interface()
interface.initialize()
print("finished init")

#Initialise motors
backClampMotor = 0
backRotMotor = 1
frontClampMotor = 2
frontRotMotor = 3

interface.motorEnable(0)
interface.motorEnable(1)
#interface.motorEnable(frontClampMotor)
#interface.motorEnable(frontRotMotor)
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


#interface.increaseMotorAngleReferences([backClampMotor, frontClampMotor],[-2,-2])

host = ''
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host,port))
s.listen(1)
while True:
	conn, addr = s.accept()
	print('Connected by', addr)
	conn.sendall(b'clampBoth')
	
	conn.recv(1024)
	conn.sendall(b'releaseBoth')
	#data = conn.recv(1024)
	#if not data: continue
	#conn.sendall(data)
conn.close()


