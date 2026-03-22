#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

bool validate_args_and_print_info(int argc, char* argv[]) {
    if (argc < 2) {
         printf("Usage: %s [directory]", argv[0])
    }
}

int main(int argc, char *argv[]) {
    if (!validate_args_and_print_info(argc, argv)) {
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}
