#include <err.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>

#include "config.h"

int sockfd = -1;

void cleanup() {
    close(sockfd);
}

void interrupt_and_terminate_handler(int signo) {
    if (signo != SIGTERM && signo != SIGINT) {
        return;
    }

    if (signo == SIGTERM) {
        write(1, "Terminating...\n", 16);
    } else if (signo == SIGINT) {
        write(1, "Interrupting...\n", 17);
    }
    
    exit(0);    // invoke cleanup() bc we register it via atexit()
}


void sigpipe_handler(int signo) {
    if (signo == SIGPIPE) {
        write(2, "Connection was closed\n", 22);
        exit(1);
    }
}


// signal handlers calls exit(2) for quiting. 
// don't forget to add cleanup fucntion by atexit(2) 
void register_signal_handlers() {
    sigset_t mask;
    sigemptyset(&mask);
    sigaddset(&mask, SIGINT);
    sigaddset(&mask, SIGTERM);
    sigaddset(&mask, SIGPIPE);
    
    struct sigaction term_and_int = {
        .sa_handler = interrupt_and_terminate_handler,
        .sa_mask = mask,
        .sa_flags = 0
    };
    sigaction(SIGINT, &term_and_int, NULL);
    sigaction(SIGTERM, &term_and_int, NULL);

    struct sigaction pipe = {
        .sa_handler = sigpipe_handler,
        .sa_mask = mask,
        .sa_flags = 0
    };
    sigaction(SIGPIPE, &pipe, NULL);

}

int main() {
    register_signal_handlers();
    atexit(cleanup);

    int sockfd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (sockfd == -1) {
        err(1, "Cannot create socket");
    }
    struct sockaddr_un servaddr = {
        .sun_family = AF_UNIX,
        .sun_path = SERVER_SOCK_PATH
    };

    if (connect(sockfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) == -1) {
        err(1, "connect" SERVER_SOCK_PATH);
    }

    for (;;) {
        char sendline[MSG_LEN + 1];
        char recvline[MSG_LEN + 1];
        
        fgets(sendline, MSG_LEN, stdin);

        if (write(sockfd, sendline, strlen(sendline) + 1) == -1) {
            perror("ERROR: Cannot write");
            break;
        }

        ssize_t nread = read(sockfd, recvline, MSG_LEN);
        if (nread == -1) {
            perror("ERROR: Cannot read");
            break;
        }
        if (nread == 0) {
            puts("Connection ended");
            break;
        }
        puts(recvline);
    }
    close(sockfd);

    return 0;
}