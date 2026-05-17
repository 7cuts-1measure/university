#include <err.h>
#include <stdio.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>

#include "config.h"

int main() {

    int sockfd = socket(AF_UNIX, SOCK_STREAM, 0);
    struct sockaddr_un servaddr = {
        .sun_family = AF_UNIX,
        .sun_path = SERVER_SOCK_PATH
    };

    if (connect(sockfd, (struct sockaddr *) &servaddr, sizeof(servaddr)) == -1) {
        err(1, "connect " SERVER_SOCK_PATH);
    }

    for (;;) {
        char sendline[MSG_LEN];
        char recvline[MSG_LEN];
        fgets(sendline, MSG_LEN, stdin);

        write(sockfd, sendline, strlen(sendline) + 1);

        read(sockfd, recvline, MSG_LEN);
        puts(recvline);
    }

    return 0;
}