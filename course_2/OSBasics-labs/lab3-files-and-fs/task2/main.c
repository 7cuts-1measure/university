#include <linux/limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <err.h>
#include <dlfcn.h>
#include <unistd.h>
#include <string.h>

// OMG It is kinda like interface:
// if function does not match this type -> it is invalid
typedef int (*fp_func)(int argc, char* argv[]);

void print_help() {
    puts("Help message");
}

const char* last_component(const char* path) {
    int len = strlen(path);
    int i;
    for (i = len - 1; i >= 0; --i) {
        if (path[i] == '/')
            break;
    }
    return path + i + 1;
}

bool has_help_flag(int argc, char **argv) {
    for (int i = 0; i < argc; ++i) {
        if (!strcmp(argv[i], "--help") || !strcmp(argv[i], "-h"))
            return true;
    }
    return false;
}

#include <dirent.h>
#include <err.h>
#include <fcntl.h>
#include <linux/limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <unistd.h>

static const char *file_type(struct stat s) {
    switch (s.st_mode & S_IFMT) {
        case S_IFBLK:  return "block device";
        case S_IFCHR:  return "char device ";
        case S_IFDIR:  return "directory   ";
        case S_IFIFO:  return "FIFO/pipe   ";
        case S_IFLNK:  return "symlink     ";
        case S_IFREG:  return "regular file";
        case S_IFSOCK: return "socket      ";
        default:       return "unknown     ";
    }
}

static int ls_one(const char *file_name) {
    int exit_code = 0;
    
    struct stat s;
    if (lstat(file_name, &s) == -1) {
        warn("cannot get stat of %s", file_name);
        return 1;
    }
    
    if (!S_ISDIR(s.st_mode)) {
        printf("%s\t%s\n", file_type(s), file_name);
        return 0;
    }

    DIR *dirp = opendir(file_name);
    if (!dirp) {
        warn("%s", file_name);
        return 1;
    }

    int dfd = open(file_name, O_DIRECTORY | O_RDONLY);
    if (dfd == -1) {
        warn("%s", file_name);
        closedir(dirp);
        return 1;
    }

    struct dirent *d = NULL;
    for (;;) {
        d = readdir(dirp);
        if (!d)
            break;

        if (fstatat(dfd, d->d_name, &s, AT_SYMLINK_NOFOLLOW) == -1) {
            warn("cannot get stat of %s", file_name);
            exit_code = 1;
            continue;
        }
        printf("%s\t%s\n", file_type(s), d->d_name);
    }

    close(dfd);
    closedir(dirp);
    return exit_code;
}

int fp_ls(int argc, char *argv[])
{
    int exit_code = 0;
    if (argc == 1)
        return ls_one(".");

    for (int i = 1; i < argc; ++i) {
        if (argc > 2) 
            printf("%s:\n", argv[i]);
        
        if(ls_one(argv[i]) != 0)
            exit_code = 1;

        if (argc > 2) {
            printf("\n");
        }
    }

    return exit_code;
}


int main(int argc, char *argv[])
{
    if (has_help_flag(argc, argv)) {
        print_help();
        return 0;
    }
    
    const char* exe_name = last_component(argv[0]);

    void *handle = dlopen("./libfp.so", RTLD_LAZY);
    
    if (!handle)
        errx(1, "%s\n", dlerror());
    
    dlerror(); /* Clear errors */

    char *funcname = NULL;
    asprintf(&funcname, "fp_%s", exe_name);
    
    fp_func func = dlsym(handle, funcname);
    free(funcname);
    
    if (!func) {
        fprintf(stderr, "Unknown function: `%s`\nType --help to list all functions\n", exe_name);
        return 1;
    }
    return func(argc, argv);
}