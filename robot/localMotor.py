import time
import const

def wait(motors, interface):
        while not interface.motorAngleReferencesReached(motors):
                time.sleep(0.1)

def waitNonblock(motors, interface):
        prev = [tups[0] for tups in interface.getMotorAngles(motors)]
        while not interface.motorAngleReferencesReached(motors):
                time.sleep(0.1)
                angles = [tups[0] for tups in interface.getMotorAngles(motors)]
                if(angles == prev):
                        break
                else:
                        prev = angles

def clampAngle(motors, interface, angle):
	interface.increaseMotorAngleReferences(motors,[angle]*len(motors))
        waitNonblock(motors, interface)
        interface.increaseMotorAngleReferences(motors,[0]*len(motors))

def clamp(motor, interface):
	clampAngle(motor, interface, -const.clamp)


def releaseAngle(motors, interface, angle):
	interface.increaseMotorAngleReferences(motors, [angle]*len(motors))
        wait(motors, interface)

def release(motors, interface):
	releaseAngle(motors, interface, const.clamp)

def halfClamp(motors, interface):
	releaseAngle(motors, interface, -const.clamp/2)

def halfRelease(motors, interface):
	releaseAngle(motors, interface, const.clamp/2)

def turnClockwise(motors, clamp, release, interface):
        interface.increaseMotorAngleReferences(motors,[-const.quarterTurn]*len(motors))
        wait(motors, interface)
        release()
        interface.increaseMotorAngleReferences(motors,[const.quarterTurn]*len(motors))
        wait(motors, interface)
        clamp()


