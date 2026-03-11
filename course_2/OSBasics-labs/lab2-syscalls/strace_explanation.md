# Эксперименты

## Вывод strace

Запустим hello world
`strace ./hello.out`

### execve

> execve("./hello.out", ["./hello.out"], 0x7ffd49e430d0 /* 53 vars */) = 0

Запускает программу. То есть аоздёт процесс, который потом будет выполнять процессор.

- Аргументы
    1. путь к исполняемому фалйлу
    2. массив argv.
    3. environment vars: ["SHELL=/bin/fish", "USER=kapiuser", "LANG=en.US.UTF-8", ...].     

Есть утилита `env`, которая может вывести список всех переменных.
Суть в том, что каждый процесс порождается с помощью этого системного вызова. 
Стало быть, есть процессы, которые должны создать эти переменные окружения и передать своим
процессам потомкам. Например, самый первый процесс (в моём случае это `systemd`)


### brk
brk(NULL)                               = 0x55955d9d8000

`brk(void* addr)` - изменить конец памяти, доступной процессу. Сюда входит и стек, и куча.

- Аргументы: `void* addr` - адрес, куда установить break. Однако `brk()` не обязан его использовать.
- Возвращает: Указатель на новый break в случае успеха. В случае ошибки возвращает текущий break.

### access
access("/etc/ld.so.preload", R_OK)      = -1 ENOENT (No such file or directory)

`access(char* path, int mode)`. Проверяет правад доступа пользователя к файлу по U_ID процесса.
- Аргументы
    1. путь к файлу
    2. Режим проверки. Задефайнено в `fcntl.h` (file control)
        1. R_OK - проверка на права чтения
        2. W_OK - проверка на запись
        3. X_OK - проверка на выполнение
        4. F_OK - проверка на существование.
- Возвращает: 0, если всё ок, иначе -1 и высставляет `errno`


### openat
openat(AT_FDCWD, "/etc/ld.so.cache", O_RDONLY|O_CLOEXEC) = 3

`openat(char)` *открывает* файл и возвращает файловый дескриптор
- Аргументы
    1. dirfd - файловый дескриптор директории, откуда строить путь
    2. путь к файлу (отсносительный или абсолютный, если начинается с `/`
    3. режимы открытия. O_CLOEXEC - close on exec. Закрыть файл в новом процессе, 
    если процесс замещается другим. Нарпимер, с помощью `execve`

### fstat
fstat(3, {st_mode=S_IFREG|0644, st_size=162607, ...}) = 0

`fstat` - получить информацию о файле. 
- Аргументы 
    1. `fd` - файловый дескриптор. Для каждого процесса хранится таблица
        файловых дескрипторов и соответсвующих им файлов. 
        Структура, ассоциированная с файловым дескриптором содержит
        функции `read`, `write`, и так далее - то, что мы вызываем, когда хотим поработать с файлом.
    2. `&struct stat` - **выходной параметр** - структура со статой файла.
- Возвращает 0, если ок, иначе -1 и выставляет `errno`

### mmap
mmap(NULL, 162607, PROT_READ, MAP_PRIVATE, 3, 0) = 0x7f8242c59000

`mmap` - отображает файл в адресное пространство процесса. 
- Аргументы: 
    1. подсказка начального адреса 
    2. Число байт - сколько выделить
    3. prot - protection. PROT_EXEC, PROT_READ, PROT_WRITE, PROT_NONE.
    4. flags - всякие разные флаги
#### флаги
    - MAP_SHARED - разделяемое отображение. если несколько процессов мапят один регион
    - MAP_PRIVATE - copy-on-write отображение.

Примерный алгоритм
=================

1. Обращение к памяти (`char c = *ptr;`).
2. Процессор смотрит в таблицу страниц. Эта страница помечена как "Not Present" 
    и тогда процессор генерит прерывание.
3. Управление переходит в обработчки `do_page_fault`
4. Далее обработчик подгружает файл с диска в память.

*в 4 пункте ещё есть page cache*
=================


### close(int fd)
close(3)                                = 0

Закрывайет файловый дескриптор. теперь fd не ассоциирован с файлом и может быть 
переиспользован. Если на файл больше не ссылается ни один файловый дескриптор, то ресурсы, нужные
для структуры этого файла освобождаются.


openat(AT_FDCWD, "/usr/lib/libc.so.6", O_RDONLY|O_CLOEXEC) = 3

### read(int fd, void buf[count], size_t count) -> сколько прочитано
читает из файла до `count` байт. файл находится по файловому дескриптору
- Возвращает -1 если ошибка и выставляет `errno`


read(3, "\177ELF\2\1\1\3\0\0\0\0\0\0\0\0\3\0>\0\1\0\0\0\0y\2\0\0\0\0\0"..., 832) = 832
pread64(3, "\6\0\0\0\4\0\0\0@\0\0\0\0\0\0\0@\0\0\0\0\0\0\0@\0\0\0\0\0\0\0"..., 896, 64) = 896
**pread - просто с offsetом**

fstat(3, {st_mode=S_IFREG|0755, st_size=2149504, ...}) = 0
mmap(NULL, 8192, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_ANONYMOUS, -1, 0) = 0x7f8242c57000
pread64(3, "\6\0\0\0\4\0\0\0@\0\0\0\0\0\0\0@\0\0\0\0\0\0\0@\0\0\0\0\0\0\0"..., 896, 64) = 896
mmap(NULL, 2173808, PROT_READ, MAP_PRIVATE|MAP_DENYWRITE, 3, 0) = 0x7f8242a00000
mmap(0x7f8242a24000, 1511424, PROT_READ|PROT_EXEC, MAP_PRIVATE|MAP_FIXED|MAP_DENYWRITE, 3, 0x24000) = 0x7f8242a24000
mmap(0x7f8242b95000, 458752, PROT_READ, MAP_PRIVATE|MAP_FIXED|MAP_DENYWRITE, 3, 0x195000) = 0x7f8242b95000
mmap(0x7f8242c05000, 24576, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_FIXED|MAP_DENYWRITE, 3, 0x204000) = 0x7f8242c05000
mmap(0x7f8242c0b000, 31600, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_FIXED|MAP_ANONYMOUS, -1, 0) = 0x7f8242c0b000
close(3)                                = 0
mmap(NULL, 12288, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_ANONYMOUS, -1, 0) = 0x7f8242c54000

### arch_prctl(int op, ulong addr)
arch_prctl(ARCH_SET_FS, 0x7f8242c54740) = 0
set_tid_address(0x7f8242c54d68)         = 329010
set_robust_list(0x7f8242c54a20, 24)     = 0
rseq(0x7f8242c546a0, 0x20, 0, 0x53053053) = 0
mprotect(0x7f8242c05000, 16384, PROT_READ) = 0
mprotect(0x559541d57000, 4096, PROT_READ) = 0
mprotect(0x7f8242cc2000, 8192, PROT_READ) = 0
prlimit64(0, RLIMIT_STACK, NULL, {rlim_cur=8192*1024, rlim_max=RLIM64_INFINITY}) = 0
getrandom("\x7d\x25\x25\xe1\xbb\x15\x2a\xcd", 8, GRND_NONBLOCK) = 8
munmap(0x7f8242c59000, 162607)          = 0
fstat(1, {st_mode=S_IFCHR|0666, st_rdev=makedev(0x1, 0x3), ...}) = 0
ioctl(1, TCGETS2, 0x7ffc5e175120)       = -1 ENOTTY (Inappropriate ioctl for device)
brk(NULL)                               = 0x55955d9d8000
brk(0x55955d9fa000)                     = 0x55955d9fa000
write(1, "hello, world!\n", 14)         = 14
exit_group(0)                           = ?
+++ exited with 0 +++

