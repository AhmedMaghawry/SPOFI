from socket import *
import spotfire

# set parameters
HOST = "localhost" #local host
PORT = 7000 #open port 7000 for connection

#initialize
serverSocket = socket(AF_INET, SOCK_STREAM)
serverSocket.bind((HOST, PORT))
serverSocket.listen(1) #how many connections can it receive at one time

spotfire.update_report_state("-LPDCl8toHAozLUOmr57")
#
"""
print('The server is ready to receive')
while True:
	connectionSocket, addr = serverSocket.accept()
	print('accepted request from:', addr)
	report_id = connectionSocket.recv(1024).decode()
	spotfire.update_report_state(report_id)
	connectionSocket.close()

"""

