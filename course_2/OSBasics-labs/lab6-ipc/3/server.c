#include <asm-generic/errno-base.h>
#include <assert.h>
#include <errno.h>
#include <err.h>
#include <signal.h>
#include <stdatomic.h>
#include <stdlib.h>
#include <sys/poll.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <stdbool.h>
#include <stdio.h>
#include <sys/signal.h>
#include <poll.h>
#include "config.h"

// 1. Do echo-server for one connection
// 2. ADD: listent another connection after closing the first one
// 3. ADD: handle multiple connection simultaneously



// ========== GLOBAL VARS ============ //
int sockfd;

bool is_socket_created = false;
bool is_socket_binded = false;
// ---------------------------------- //


// ========== Structrues ============ //
typedef struct {
    struct pollfd *fds;
    int count;
    int capacity;
} Connections;
// ---------------------------------- //

void add_connection(Connections *c, struct pollfd fd) {
    assert(c != NULL);
    assert(c->count <= c->capacity);

    if (c->count == c->capacity) {
        c->capacity = c->capacity == 0 ? CONECTIONS_INIT_CAPACITY : c->capacity * 2;
        c->fds = realloc(c->fds, c->capacity * sizeof(c->fds[0]));
        if (c->fds == NULL) {
            err(1, "ERROR: Cannot allocate memory for new connection");
        }

    }
    c->fds[c->count++] = fd;
}


void cleanup() {
    if (is_socket_created) {
        puts("Closing socket");
        if (close(sockfd) == -1) {
            perror("close");
            _exit(3);
        }
    }
    
    if (is_socket_binded) {
        puts("Unlink socket path");
        if (unlink(SERVER_SOCK_PATH) == -1) {
            perror("ERROR: Cannot unlink "SERVER_SOCK_PATH);
            _exit(3);
        }
    }
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

// signal handlers calls exit(2) for quiting. 
// don't forget to add cleanup fucntion by atexit(2) 
void register_signal_handlers() {
    sigset_t mask;
    sigemptyset(&mask);
    sigaddset(&mask, SIGINT);
    sigaddset(&mask, SIGTERM);
    struct sigaction act = {
        .sa_handler = interrupt_and_terminate_handler,
        .sa_mask = mask,
        .sa_flags = 0
    };
    sigaction(SIGINT, &act, NULL);
    sigaction(SIGTERM, &act, NULL);
}

void do_echo_ones(int *client_fd) {
    char msg[MSG_LEN];
    ssize_t nread = read(*client_fd, msg, sizeof(msg));
    if (nread == -1) {
        warn("WARN: read from clientfd");
        close(*client_fd);
        *client_fd = -1; // to not polling it!
    } 
    if (nread == 0) {
        puts("Connection closed");
        close(*client_fd);
        *client_fd = -1; // to not polling it
        return;
    }
    write(*client_fd, msg, nread);
}


int main() {
    register_signal_handlers(); 
    atexit(cleanup);

    sockfd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (sockfd == -1) {
        err(1, "ERROR: socket");
    }
    is_socket_created = true;

    struct sockaddr_un my_addr = {
        .sun_family = AF_UNIX,
        .sun_path = SERVER_SOCK_PATH
    };
    
    if (bind(sockfd, (struct sockaddr *)&my_addr, sizeof(my_addr)) == -1) {
        err(1, "ERROR: bind to " SERVER_SOCK_PATH);
    }
    is_socket_binded = true;
    
    if (listen(sockfd, LISTEN_BACKLOG) == -1) {
        err(1, "ERROR: listen");
    }
    puts("Listening connections on "SERVER_SOCK_PATH);


 
    // now we can accept incoming connections
 
    Connections cons = {0};
    struct pollfd listener = {
        .fd = sockfd,
        .events = POLL_IN
    };
    add_connection(&cons, listener);
    
    while (true) {
        int num_polled = poll(cons.fds, cons.count, -1);   // infinite timeout
        if (num_polled < 0) {
            switch (errno) {
                case EINTR: continue;
                default: err(1, "ERROR: poll");
            }
        }
        int cnt = 0;   // check only num_polled events
        // check listener
        if (cons.fds[0].revents & (POLLIN) ) {
            cnt++;
            int client_fd = accept(sockfd, NULL, NULL);
            if (client_fd == -1) {
                warn("WARN: Failed to accept a connection");
            } else {
                struct pollfd client = {client_fd, POLLIN};
                add_connection(&cons, client);
                puts("Add new connection");
            }
        }

        // check connections
        for (int i = 1; cnt < num_polled && i < cons.count; i++) {
            if (cons.fds[i].revents & POLLIN) {
                cnt++;
                do_echo_ones(&cons.fds[i].fd);
            }
            
            if (cons.fds[i].revents & POLLHUP) {
                write(1, "sock closed\n", 12);
            }
        }
    }   
}