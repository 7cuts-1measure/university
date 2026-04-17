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
#include <stdbool.h>
#include <sys/stat.h>
#include <unistd.h>


#define LOG(format, ...) printf("LOG: " format "\n", __VA_ARGS__)

#define CAT_BUF_SIZE 1024

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

int cat_one(const char* path)
{
    int exit_code = 0;
    int fd = path ? open(path, O_RDONLY) : STDIN_FILENO;
    if (fd == -1) {
        warn("%s", path);
        return 1;
    }

    for (;;) {
        char buf[CAT_BUF_SIZE];

        int nread = read(fd, buf, CAT_BUF_SIZE); 

        if (nread == -1) {
            warn("error reading");
            exit_code =1;
            break;
        }

        if (nread == 0) 
            break;

        int nwrite = write(STDOUT_FILENO, buf, nread);
        
        if (nwrite == -1) {
            warn("error writing");
            exit_code = 1;
            break;
        }        
        
    }
    if (path) close(fd);
    return exit_code;
}

int fp_cat(int argc, char *argv[])
{
    int exit_code = 0;
    if (argc == 1) 
        exit_code = cat_one(NULL);

    for (int i = 1; i < argc; ++i) {
        if (cat_one(argv[i]) != 0) 
            exit_code = 1;

        printf("\n");
    }
    return exit_code;
}

int rm_one(const char *path) 
{
    if (unlink(path) == -1) {
        warn("%s", path);
        return 1;
    }
    return 0;
}

int fp_rm(int argc, char *argv[])
{
    int exit_code = 0;
    if (argc < 2) {
        warnx("missing file operand");
        return 1;
    }

    for (int i = 1; i < argc; ++i) {
        if (rm_one(argv[i]) != 0)
            exit_code = 1;
    }
    return exit_code;
    
}

int fp_symlink(int argc, char *argv[])
{
    if (argc < 2) {
        warnx("missing target and linkpath");
        return 1;
    } else if (argc < 3) {
        warnx("missing linkpath");
        return 1;
    }

    const char *target = argv[1];
    const char *linkpath =argv[2];
    
    if (symlink(target, linkpath) == -1) {
        warn("filed to create symlink from '%s' to '%s'", target, linkpath);
        return 1;
    }
    return 0;
}


int fp_readsymlink(int argc, char *argv[]) 
{
    if (argc < 2) {
        warnx("missing file operand");
        return 1;
    }

    char buf[PATH_MAX];
    int nread = readlink(argv[1], buf, PATH_MAX);
    if (nread == -1) {
        warn("%s", argv[1]);
        return 1;
    }
    int nwrite = write(STDOUT_FILENO, buf, nread);
    puts("");
    if (nwrite != nread) {
        warn("%s", argv[1]);
        return 1;
    }
    
    return 0;
}


bool is_symlink(const char *path)
{
    struct stat s;
    if (lstat(path, &s) == -1) {
        puts("lstat == -1");
        warn("%s", path);
        return false;
    }

    return S_ISLNK(s.st_mode);
}

int fp_catsymlink(int argc, char *argv[])
{
    if (argc < 2) {
        warnx("missing file operand");
        return 1;
    }

    if (!is_symlink(argv[1])) {
        warnx("'%s' is not a symbolic link", argv[1]);
        return 1;
    }

    cat_one(argv[1]);
    return 0;
}


