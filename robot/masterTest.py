import socket
import const

host = ''
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host,port))
s.listen(1)
conn, addr = s.accept()
print('Connected by', addr)
while True:
	data = conn.recv(1024)
	if not data:
		print 'Data is invalid' 
		break
	conn.sendall(data)
conn.close()


