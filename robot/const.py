#Clamp constant
LeftClamp = 0
RightClamp = 1
FrontClamp = 2
BackClamp = 3

#Release constants
LeftRelease = 'releaseL'
RightRelease = 'releaseR'
FrontRelease = 'releaseF'
BackRelease =  'releaseR'

#Turn constants
U = 8
F = 9
R = 10
D = 11
B = 12
L = 13

U2 = 'U2'
F2 = 'F2'
R2 = 'R2'
D2 = 'D2'
B2 = 'B2'
L2 = 'L2'

UPrime = 'UPrime'
FPrime = 'FPrime'
RPrime = 'RPrime'
DPrime = 'DPrime'
BPrime = 'BPrime'
LPrime = 'LPrime'
 
#Other
quarterTurn = 2.3
clamp = 2
turnSpeed = 6


#Motor params
rotPeriod = 2
rotKP = 450.0

def getRotationParams(interface):
	rotMotorParams = interface.MotorAngleControllerParameters()
	rotMotorParams.maxRotationAcceleration = 20.0
	rotMotorParams.maxRotationSpeed = 180.0
	rotMotorParams.feedForwardGain = 255/20.0
	rotMotorParams.minPWN = 20.0
	rotMotorParams.pidParameters.minOutput = -255
	rotMotorParams.pidParameters.maxOutput = 255
	rotMotorParams.pidParameters.k_p = rotKP
	rotMotorParams.pidParameters.k_i = 2*rotKP/rotPeriod
	rotMotorParams.pidParameters.k_d = rotKP*rotPeriod/3
	return rotMotorParams

clampPeriod = 2
clampKP = 400.0

def getClampParams(interface):
	clampMotorParams = interface.MotorAngleControllerParameters()
	clampMotorParams.maxRotationAcceleration = 20.0
	clampMotorParams.maxRotationSpeed = 180.0
	clampMotorParams.feedForwardGain = 255/20.0
	clampMotorParams.minPWM = 25.0
	clampMotorParams.pidParameters.minOutput = -255
	clampMotorParams.pidParameters.maxOutput = 255
	clampMotorParams.pidParameters.k_p = clampKP
	clampMotorParams.pidParameters.k_i = 0
	clampMotorParams.pidParameters.k_d = 0		
	return clampMotorParams
