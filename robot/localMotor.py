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
        interface.setMotorAngleReferences(motors,interface.getMotorAngleReferences(motors))

def clamp(motor, interface):
	clampAngle(motor, interface, -const.clamp)


def releaseAngle(motors, interface, angle):
	interface.increaseMotorAngleReferences(motors, [angle]*len(motors))
        wait(motors, interface)

def release(motors, interface):
	releaseAngle(motors, interface, const.clamp)

def halfClamp(motors, interface):
	clampAngle(motors, interface, -const.clamp/2)

def halfRelease(motors, interface):
	releaseAngle(motors, interface, const.clamp/2)

def turnClockwise(motors, clamp, release, interface):
        turn(motors, clamp, release, -const.quarterTurn, interface)

def turn(motors, clamp, release, amount,interface):
	interface.increaseMotorAngleReferences(motors,[amount]*len(motors))
        wait(motors, interface)
        release()
        interface.increaseMotorAngleReferences(motors,[-amount]*len(motors))
        wait(motors, interface)
        clamp()

def turnClockwise(motors, clamp, release, interface):
        turn(motors, clamp, release, -const.quarterTurn, interface)

def turnAnti(motors, clamp, release, interface):
	turn(motors, clamp, release, const.quarterTurn, interface)

