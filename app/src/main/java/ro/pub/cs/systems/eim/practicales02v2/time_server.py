import socket
from datetime import datetime
import time

# Configurarea serverului
HOST = "0.0.0.0"  # Ascultă pe toate interfețele rețelei locale
PORT = 12345       # Portul pe care serverul va asculta conexiunile

def start_server():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
        server_socket.bind((HOST, PORT))
        server_socket.listen(5)  # Poate asculta până la 5 conexiuni simultane
        print(f"Server is running on {HOST}:{PORT}")

        while True:
            client_socket, client_address = server_socket.accept()
            print(f"Client connected from {client_address}")

            with client_socket:
                while True:
                    try:
                        # Trimite data și ora curentă
                        current_time = datetime.now().strftime("%H:%M:%S")
                        client_socket.sendall(f"{current_time}\n".encode("utf-8"))
                        time.sleep(1)  # Pauză de 1 secundă înainte de următorul mesaj
                    except BrokenPipeError:
                        print(f"Connection lost with {client_address}")
                        break

if __name__ == "__main__":
    start_server()
