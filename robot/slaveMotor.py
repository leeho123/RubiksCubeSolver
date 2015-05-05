
def sendCommand(socket, command,func=None):
        socket.sendall(command)      
        if func is not None:
                func()
        socket.recv(1024)

def clampBoth(socket, func=None):
	sendCommand(socket, b'clampBoth', func)

def releaseBoth(socket, func=None):
	sendCommand(socket, b'releaseBoth', func)

def turnRightClockwise(socket, func=None):
	sendCommand(socket, b'turnRightClockwise', func)

def turnLeftClockwise(socket, func=None):
	sendCommand(b'turnLeftClockwise', func)

def turnLeftAnti(socket, func=None):
	sendCommand(socket, b'turnLeftAnti', func)

def turnRightAnti(socket, func=None):
	sendCommand(socket, b'turnRightAnti', func)

def sendCommand(socket, command,func=None):
	socket.sendall(command)
	if func is not None:
		func()
	socket.recv(1024)
