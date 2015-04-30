import socket
import const

host = ''
port = 12345
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host,port))
s.listen(1)
while True:
	conn, addr = s.accept()
	print('Connected by', addr)
	conn.sendall('
	#data = conn.recv(1024)
	#if not data: continue
	#conn.sendall(data)
conn.close()


