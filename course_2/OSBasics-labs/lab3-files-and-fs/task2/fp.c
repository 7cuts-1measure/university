/*
 * fp = File Processing
 * 
 * All plugin functions should start with fp_rmdir 
 * User creates hardlink to main executable and it
 * fp.so and trying to find function with the name of 
 * executable but with prefix fp_
 *
 * For example: mkdir a b c --> fp_mkdir(argc=4, argv=["mkdir", "a", "b", "c"])
*/

#include <dirent.h>
#include <err.h>
#include <fcntl.h>
#include <linux/limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <unistd.h>


#define LOG(format, ...) printf("LOG: " format "\n", __VA_ARGS__)

// rmdir DIR_PATH
int fp_rmdir(int argc, char *argv[])
{
    int exit_code = 0;
    if (argc < 2) {
        warnx("Usage:\n    %s [dir1 [dir2 dir3 ...]]\n", argv[0]);
        return 1;
    }
    
    for (int i = 1; i < argc; i++) {
        const char *path = argv[i]; 
        LOG("rmdir %s", path);
        
        if (rmdir(path) == -1) {
            warn("%s", path);
            exit_code = 1;
        }
    }
    return exit_code;
}

int fp_mkdir(int argc, char *argv[])
{
    int exit_code = 0;
    if (argc < 2) {
        fprintf(stderr, "Usage:\n    %s [dir1 [dir2 dir3 ...]]\n", argv[0]);
        return 1;
    }

    for (int i = 1; i < argc; i++) {
        LOG("mkdir %s", argv[i]);
        
        struct stat s;
        fstatat(AT_FDCWD, ".", &s, 0);
        
        if (mkdirat(AT_FDCWD, argv[i], s.st_mode & 0777) == -1) {
            warn("cannot create directory '%s'", argv[i]);
            exit_code = 1;
        };
    }
    return exit_code;
}


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

int fp_touch(int argc, char *argv[]) 
{
    int exit_code = 0;
    if (argc < 2) {
        warnx("missing file operand");
        return 1;
    }
    for (int i = 1; i < argc; ++i) {
        // create if not exists
        int fd = open(argv[i], O_CREAT | O_RDONLY, 0644);    
        if (fd == -1) {
            warn("%s", argv[i]);
            exit_code = 1;
        }
        close(fd);
    }
    return exit_code;
    
}
