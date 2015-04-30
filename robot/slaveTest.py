import brickpi
import socket
import time

host = '192.168.0.32'
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((host,port))
s.sendall(b'Hello world')
data = s.recv(1024)
s.close()
print('Received', repr(data))

