# Переменные
JC = javac
JAR = jar
JFLAGS = -d $(BUILD_DIR) -sourcepath src
RM = rm -rf

# Директории
SRC_DIR = src
BUILD_DIR = build
JAR_DIR = jar
COMMON_SRC = $(SRC_DIR)/common
SERVER_SRC = $(SRC_DIR)/server
CLIENT_SRC = $(SRC_DIR)/client

# Все исходники common (если нужно явно перечислить, но можно найти)
COMMON_JAVA = $(wildcard $(COMMON_SRC)/*.java)
SERVER_JAVA = $(wildcard $(SERVER_SRC)/*.java)
CLIENT_JAVA = $(wildcard $(CLIENT_SRC)/*.java)

# Цели по умолчанию
all: server.jar client.jar

# Создание server.jar
server.jar: compile-server
	$(JAR) cfe $(JAR_DIR)/server.jar server.ServerApp -C $(BUILD_DIR) server -C $(BUILD_DIR) common
	@echo ":: Created $(JAR_DIR)/server.jar"

# Создание client.jar
client.jar: compile-client
	$(JAR) cfe $(JAR_DIR)/client.jar client.ClientApp -C $(BUILD_DIR) client -C $(BUILD_DIR) common
	@echo ":: Created $(JAR_DIR)/client.jar"

# Компиляция общих классов (нужна для server и client)
compile-common:
	mkdir -p $(BUILD_DIR)
	if [ -n "$(COMMON_JAVA)" ]; then $(JC) $(JFLAGS) $(COMMON_JAVA); else true; fi

# Компиляция сервера (зависит от common)
compile-server: compile-common
	mkdir -p $(BUILD_DIR)
	$(JC) $(JFLAGS) $(SERVER_JAVA)

# Компиляция клиента (зависит от common)
compile-client: compile-common
	mkdir -p $(BUILD_DIR)
	$(JC) $(JFLAGS) $(CLIENT_JAVA)

# Запуск сервера (удобная цель)
run-server: server.jar
	java -jar $(JAR_DIR)/server.jar

# Запуск клиента
run-client: client.jar
	java -jar $(JAR_DIR)/client.jar

# Очистка
clean:
	$(RM) $(BUILD_DIR) $(JAR_DIR)

# Защита от конфликта с файлами, имеющими имена целей
.PHONY: all compile-common compile-server compile-client run-server run-client clean
