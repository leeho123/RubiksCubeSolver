def clampBoth(socket, func=None):
	socket.sendall(b'clampBoth')
	if func is not None:
		func()
	socket.recv(1024)

def releaseBoth(socket, func=None):
	socket.sendall(b'releaseBoth')
        if func is not None:
                func()
        socket.recv(1024)

def turnRightClockwise(socket, func=None):
	socket.sendall(b'turnRightClockwise')
	if func is not None:
		func()
	socket.recv(1024)

def turnLeftClockwise(socket, func=None):
	socket.sendall(b'turnLeftClockwise')
	if func is not None:
		func()
	socket.recv(1024)
