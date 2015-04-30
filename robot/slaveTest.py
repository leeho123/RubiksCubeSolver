import brickpi
import socket
import time
import const

host = '192.168.0.32'
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((host,port))

#s.sendall(b'Hello world')
while True:
	#Wait to get instructions
	data = s.recv(1024)
	
s.close()
print('Received', repr(data))

