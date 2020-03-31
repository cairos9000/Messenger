from socket import AF_INET, socket, SOCK_STREAM
from threading import Thread


def broadcastMessage(msg, name="Bot master: "):  # send message to all
    history.append(name + msg.decode("utf8"))
    for sock in clients:
        sock.send(bytes(name, "utf8") + msg + bytes("{&}", "utf8"))


def disconnectFunc(client):  # disconnect some client.
    global clients, addresses
    nickname = clients[client]
    del clients[client]
    broadcastMessage(bytes("{} has left the chat.".format(nickname), "utf8"))
    print("{}:{} has disconnected.".format(addresses[client][0], addresses[client][1]))
    del addresses[client]
    client.close()


def handleClient(client):  # handle every connected client.
    global buferSize, clients
    try:
        name = client.recv(buferSize).decode("utf8")
    except ConnectionResetError:
        print("{}:{} has disconnected.".format(addresses[client][0], addresses[client][1]))
        del addresses[client]
        return True
    welcomeString = "Type {quit} to exit. History:{&}"
    try:
        for s in history:
            if len(welcomeString + s) > 2044:
                client.send(bytes(welcomeString + "{&}", "utf8"))
                welcomeString = ""
            welcomeString += s;
        client.send(bytes(welcomeString, "utf8"))
    except ConnectionResetError:
        print("{}:{} has disconnected.".format(addresses[client][0], addresses[client][1]))
        del addresses[client]
        return True
    msg = "{} has joined".format(name)
    broadcastMessage(bytes(msg, "utf8"))
    clients[client] = name
    while True:
        try:
            msg = client.recv(buferSize)
        except ConnectionResetError:
            disconnectFunc(client)
            return True
        if msg != bytes("{quit}", "utf8"):
            broadcastMessage(msg, name=name + ": ")
        else:
            try:
                client.send(bytes("{quit}", "utf8"))
            except ConnectionResetError:
                pass
            disconnectFunc(client)
            return True


def acceptingConnections():
    global addresses
    while True:
        client, client_address = server.accept()
        print("{}:{} has connected.".format(client_address[0], client_address[1]))
        try:
            client.send(bytes("Type nick and send it{&}", "utf8"))
        except ConnectionResetError:
            print("{}:{} has disconnected.".format(addresses[client][0], addresses[client][1]))
            client.close()
            continue
        addresses[client] = client_address
        clientThread = Thread(target=handleClient, args=(client,))
        clientThread.start()


clients = {}
addresses = {}
history = list()
buferSize = 2048
if __name__ == "__main__":
    server = socket(AF_INET, SOCK_STREAM)
    server.bind(('', 9000))
    server.listen(10)  # number of connections.
    print("Start work")
    threadServer = Thread(target=acceptingConnections)  # start server.
    threadServer.start()
    threadServer.join()
    server.close()