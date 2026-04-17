#include <asm-generic/errno-base.h>
#include <assert.h>
#include <errno.h>
#include <dirent.h>
#include <err.h>
#include <linux/limits.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <fcntl.h> 
#include <sys/syscall.h>
#include <unistd.h>

#define BUF_SIZE 1024
#define MAX_PATH_LEN 4096

typedef struct {
    __ino64_t inode_num;
    __off64_t fs_specific_offset;
    unsigned short reclen;
    unsigned char type; // better use stat(2) to explore type
    char name[];  // variable size array
} linux_dirent64;


bool validate(int argc, char* argv[]) 
{
    if (argc < 2) {
        printf("Usage: %s [directory]", argv[0]);
        return false;
    }
    return true;        
}

void str_copy_reversed(char *dst, const char *src, size_t n) 
{
    for (int i = 0; i < n; i++) {
        dst[i] = src[n - i - 1];
    }
    dst[n] = '\0';
}


void print_info(linux_dirent64 *dentry) {
    static bool printed_statistics = false;
    if (!printed_statistics) {
        printf("NOTE: `len` is not a file size! it is a `struct linux_dirent` size\n");
        printf("inode No  file type    len    name\n");
        printed_statistics = true;
    }
    
    printf("%8lu  ", dentry->inode_num);
    char dentry_type = dentry->type;    
    printf("%-10s ",(dentry_type == DT_REG)  ?  "regular"   :
                    (dentry_type == DT_DIR)  ?  "directory" :
                    (dentry_type == DT_FIFO) ?  "FIFO"      :
                    (dentry_type == DT_SOCK) ?  "socket"    :
                    (dentry_type == DT_LNK)  ?  "symlink"   :
                    (dentry_type == DT_BLK)  ?  "block dev" :
                    (dentry_type == DT_CHR)  ?  "char dev"  : "???");
    printf("%5d    %s\n", dentry->reclen, dentry->name);
}

bool is_exists(const char* path) {
    int fd = open(path, O_RDONLY);  // TODO: зачем RDONLY?
    if (fd == -1) return false; 
    close(fd);
    return true;
}

ssize_t find_last(char* str, size_t len, char c) {
    for (int i = len - 1; i >= 0; i--) {
        if (str[i] == c) return i;
    }
    return -1;
} 

void swap(char* c1, char* c2) {
    char tmp = *c1;
    *c1 = *c2;
    *c2 = tmp;
} 

void reverse(char* str, size_t len) {
    for (int i = 0; i < len / 2; i++) {
        swap(&str[i], &str[len - i - 1]);
    }
}

char *areversed(const char* str) {
    size_t len = strlen(str);
    char *res = malloc(len + 1);
    if (!res)
        err(EXIT_FAILURE, "Cannot allocate memory");
    
    for (int i = 0; i < len; ++i) {
        res[i] = str[len - 1 - i];
    }
    return res;
}

ssize_t max(ssize_t a, ssize_t b) {
    return a > b ? a : b;
}

void copy_entire_file_reversed_at(int src_dir_fd, int dst_dir_fd, const char *src_path, const char*dst_path) {
    int src_fd = openat(src_dir_fd, src_path, O_RDONLY);
    if (src_fd == -1) {
        warn("Cannot open file \"%s\" for copy", src_path);
        return;
    }
    
    struct stat src_stat;
    if (fstat(src_fd, &src_stat) == -1) {
        warn("Cannot get statistics of %s", src_path);
        close(src_fd);
        return;
    }
    int dst_fd = openat(dst_dir_fd, dst_path, O_WRONLY | O_CREAT | O_TRUNC, src_stat.st_mode);  // same mode as src file
    if (dst_fd == -1) {
        warn("Cannot create or open %s", dst_path);
        close(dst_fd);
        return;
    }
    
    const int buf_size = 10;
    char buf[buf_size + 1];
    off_t offset_src = 0;
    off_t offset_dst = src_stat.st_size;

    for (bool is_copy_end = false; !is_copy_end; is_copy_end = offset_dst == 0) {
        int nread = pread(src_fd, buf, buf_size, offset_src);
        if (nread == 0) {
            return;
        } else if (nread == -1) {
            warn("Skip %s due to read error", src_path);
            return;
        }
        assert((ssize_t)offset_dst - nread >= 0);
        offset_src += nread;
        offset_dst -= nread;
        
        reverse(buf, nread);
        
        int nwrite = pwrite(dst_fd, buf, nread, offset_dst);
        if (nwrite != nread) {
            warn("WARNING: %s corruped due to error", dst_path);
            return;
        }
    }

}

void process_dir_at(int fd, int rfd) {
    static int depth = -1;
    depth++;

    // Use malloc instead of allocating on a stack
    // because if we have a depth of recursion >= 8 and BUF_SIZE = 1024
    // then we need 8 * 1024 == 8 KBytes --> stack overflow
    char *buf = (char *) malloc(BUF_SIZE);
    for (;;) {
        
        long nread = syscall(SYS_getdents64, fd, buf, BUF_SIZE);
        if (nread == -1) {
            err(EXIT_FAILURE, "Cannot read directory");
        }
        
        if (nread == 0) {
            break;
        }

        linux_dirent64 *dentry;
        for (size_t bpos = 0; bpos < nread; bpos += dentry->reclen) {
            dentry = (linux_dirent64 *) (buf + bpos);
            
            if (!strcmp(dentry->name, ".") || !strcmp(dentry->name, ".."))
                continue;
            
            for (int i = 0; i < depth; i++)
                printf("    ");
            
            const char * reversed_name = areversed(dentry->name);
            struct stat stat;
            if (fstatat(fd, dentry->name, &stat, AT_SYMLINK_NOFOLLOW) == -1)
                err(EXIT_FAILURE, "Cannot get statistics");

            if (S_ISDIR(stat.st_mode) || S_ISREG(stat.st_mode))
                printf("%s -> %s\n", dentry->name, reversed_name);
            
            if (S_ISDIR(stat.st_mode)) {
                int next_fd = openat(fd, dentry->name, O_RDONLY | O_DIRECTORY);
                if (next_fd == -1) {
                    err(EXIT_FAILURE, "Cannot open %s", dentry->name);
                }

                int ret = mkdirat(rfd, reversed_name, stat.st_mode | S_IWUSR | S_IWGRP); // add write mode bits
                if (ret == -1 && errno != EEXIST) {
                    err(EXIT_FAILURE, "bad mkdir"); // TODO: handle errors
                }
                

                int next_rfd = openat(rfd, reversed_name, O_RDONLY | O_DIRECTORY);
                if (next_rfd == -1) {
                    err(EXIT_FAILURE, "Cannot create %s", reversed_name);
                }
                
                process_dir_at(next_fd, next_rfd);    // recursive call
            } else if (S_ISREG(stat.st_mode))
                copy_entire_file_reversed_at(fd, rfd, dentry->name, reversed_name);
        }       
    }
    free(buf);
    depth--;
}



void reverse_last_dir_in_realpath(char *realpath) {
    //    /home/user/path  ->  /home/user/htap
    size_t len = strlen(realpath);
    int idx = -1;
    for (int i = len - 1; i >= 0; i--) {
        if (realpath[i] == '/'){
            idx = i;
            break;
        }
    }
    assert(idx != -1);
    idx++;
    for (int i = 0; i < (len - idx) / 2; ++i) {
        swap(&realpath[idx + i], &realpath[len - i - 1]);
    }
}

int main(int argc, char *argv[]) {
    if (!validate(argc, argv)) {
        return EXIT_FAILURE;
    }
 
    char* dir_path = realpath(argv[1], NULL);
    
    printf("%s\n", dir_path);
    
    int fd = open(dir_path, O_RDONLY | O_DIRECTORY);
    if (fd == -1) {
        if (errno == ENOTDIR) {
            errx(EXIT_FAILURE, "%s is not a directory", argv[1]);            
        } else {
            err(EXIT_FAILURE, "Cannot open a directory %s", argv[1]);
        }
    }

    
    reverse_last_dir_in_realpath(dir_path);
    
    struct stat src_stat;
    if (fstat(fd, &src_stat) == -1) {
        err(EXIT_FAILURE, "Cannot get statistics");
    }

    int ret = mkdir(dir_path, src_stat.st_mode | S_IWUSR | S_IWGRP); // add write mode bits
    if (ret == -1 && errno != EEXIST) {
        err(EXIT_FAILURE, "bad mkdir"); // TODO: handle errors
    }
    
    int rfd = open(dir_path, O_RDONLY | O_DIRECTORY);   // TODO: создать ФАЙЛ tset и получить сломанную программу
    if (rfd == -1) {
        err(EXIT_FAILURE, "1Cannot open %s", dir_path);
    }

    printf("\n");
    process_dir_at(fd, rfd);
    free(dir_path);

    return EXIT_SUCCESS;
}
