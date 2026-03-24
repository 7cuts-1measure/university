#include <asm-generic/errno-base.h>
#include <unistd.h>
#include <sys/stat.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <dirent.h>     /* Defines DT_* constants */
#include <err.h>
#include <fcntl.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/syscall.h>
#include <sys/types.h>
#include <errno.h>
#include <assert.h>

#define BUF_SIZE 1000
#define MAX_PATH_LEN 4096

typedef struct linux_dirent {
    ino_t inode_num;
    off_t fs_specific_offset;
    unsigned short len;
    char name[];  // variable size
    //char pad;
    //char d_type;
} linux_dirent;

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


void print_info(linux_dirent *dentry) 
{
    static bool printed_statistics = false;
    if (!printed_statistics) {
        printf("NOTE: `len` is not a file size! it is a `struct linux_dirent` size\n");
        printf("inode No  file type    len    name\n");
        printed_statistics = true;
    }
    
    printf("%8lu  ", dentry->inode_num);
    char dentry_type = *((char *)dentry + dentry->len - 1); // dentry->len is length of structure in *bytes* so we shuold cast dentry to (char *)
    
    printf("%-10s ",(dentry_type == DT_REG)  ?  "regular"   :
                    (dentry_type == DT_DIR)  ?  "directory" :
                    (dentry_type == DT_FIFO) ?  "FIFO"      :
                    (dentry_type == DT_SOCK) ?  "socket"    :
                    (dentry_type == DT_LNK)  ?  "symlink"   :
                    (dentry_type == DT_BLK)  ?  "block dev" :
                    (dentry_type == DT_CHR)  ?  "char dev"  : "???");
    printf("%5d    %s\n", dentry->len, dentry->name);
}

bool is_exists(const char* path) {
    int err = open(path, O_RDONLY);
    if (err == -1)
        return false; 
    close(err);
    return true;
}


// return index of last C in STR
// return -1 if no C in STR
ssize_t find_last(char* str, size_t len, char c) {
    // idea: search first C from end of STR
    for (int i = len - 1; i >= 0; i--) {
        if (str[i] == c) {
            return i;
        }
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

void path_reverse_last(char* path, size_t *len) {
    ssize_t last = find_last(path, *len, '/');
    while (last == *len - 1) {  //   path/to/dir////  <- last character is '/'
        path[last] = '\0';
        (*len)--;
        last = find_last(path, *len, '/');
    }
    last == -1 ? reverse(path, *len) : reverse(path + last, *len - last);
}
void path_append(char *path, size_t len, const char *name) {
    path[len] = '/';
    strcpy(path + 1 + len, name);
}

ssize_t max(ssize_t a, ssize_t b) {
    return a > b ? a : b;
}

void copy_entire_file_reversed(const char*dst_path, const char *src_path) {
    printf("\"%s\" -> \"%s\"\n", src_path, dst_path);

    int src_fd = open(src_path, O_RDONLY);
    if (src_fd == -1) {
        warn("Cannot open file \"%s\" for copy", src_path);
        return;
    }
    
    struct stat src_stat;
    if (fstat(src_fd, &src_stat) == -1) {
        close(src_fd);
        warn("Cannot get statistics of %s", src_path);
        return;
    }
    int dst_fd = open(dst_path, O_WRONLY | O_CREAT | O_TRUNC, src_stat.st_mode);  // same mode as src file
    if (dst_fd == -1) {
        warn("Cannot create or open %s", dst_path);
        close(src_fd);
        return;
    }
    
    const int buf_size = 10;
    char buf[buf_size + 1];
    off_t offset_src = 0;
    off_t offset_dst = src_stat.st_size;

    for (bool is_copy_end = false; !is_copy_end; is_copy_end = offset_dst == 0) {
        int nread = pread(src_fd, buf, buf_size, offset_src);
        if (nread == 0) {
            break;
        } else if (nread == -1) {
            warn("Skip %s due to read error", src_path);
            break;
        }
        assert((ssize_t)offset_dst - nread >= 0);
        offset_src += nread;
        offset_dst -= nread;
        
        reverse(buf, nread);
        
        int nwrite = pwrite(dst_fd, buf, nread, offset_dst);
        if (nwrite != nread) {
            warn("WARNING: Dst file corruped due to error");
            break;
        }
    }
}

int main(int argc, char *argv[]) {
    if (!validate(argc, argv)) {
        return EXIT_FAILURE;
    }
    const char* dir_path = argv[1];
    int src_fd = open(dir_path, O_RDONLY | O_DIRECTORY);
    if (src_fd == -1) { 
        err(EXIT_FAILURE, "Cannot open %s", dir_path);
    }

    // reverse dir path
    size_t dir_path_len = strlen(dir_path);
    
    char src_path[MAX_PATH_LEN];
    char dst_path[MAX_PATH_LEN];
    
    strcpy(src_path, dir_path);
    strcpy(dst_path, dir_path);
    path_reverse_last(dst_path, &dir_path_len);
    
 
    int ret = syscall(SYS_mkdir, dst_path, 0777);
    if (ret == -1) {
        if (errno == EEXIST) {
            printf("Directory \"%s\" exists. Writing into it\n", dst_path);
        } else {
            err(EXIT_FAILURE, "Cannot create directory %s", dst_path);
        }
    }

    char buf[BUF_SIZE];
    long nread;
    while((nread = syscall(SYS_getdents, src_fd, buf, BUF_SIZE)) > 0) {
        
        linux_dirent *dentry;
        for (size_t buf_pos = 0; buf_pos < nread;  buf_pos += dentry->len) {
            dentry = (linux_dirent *) (buf + buf_pos);
            char dentry_type = *((char *)dentry + dentry->len - 1);
            if (dentry_type != DT_REG) {
                continue;
            }
            path_append(src_path, dir_path_len, dentry->name);
            path_append(dst_path, dir_path_len, dentry->name);
            reverse(dst_path + 1 + dir_path_len, strlen(dentry->name));
            copy_entire_file_reversed(dst_path, src_path);
        }
    }
    if (nread == -1) err(EXIT_FAILURE, "ERROR: Syscall getdents problem");
    return EXIT_SUCCESS;
}
