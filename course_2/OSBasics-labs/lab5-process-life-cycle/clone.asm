
clone:     file format elf64-x86-64


Disassembly of section .init:

0000000000401000 <_init>:
  401000:	f3 0f 1e fa          	endbr64
  401004:	48 83 ec 08          	sub    $0x8,%rsp
  401008:	48 8b 05 c9 2f 00 00 	mov    0x2fc9(%rip),%rax        # 403fd8 <__gmon_start__>
  40100f:	48 85 c0             	test   %rax,%rax
  401012:	74 02                	je     401016 <_init+0x16>
  401014:	ff d0                	call   *%rax
  401016:	48 83 c4 08          	add    $0x8,%rsp
  40101a:	c3                   	ret

Disassembly of section .plt:

0000000000401020 <clone@plt-0x10>:
  401020:	ff 35 ca 2f 00 00    	push   0x2fca(%rip)        # 403ff0 <_GLOBAL_OFFSET_TABLE_+0x8>
  401026:	ff 25 cc 2f 00 00    	jmp    *0x2fcc(%rip)        # 403ff8 <_GLOBAL_OFFSET_TABLE_+0x10>
  40102c:	0f 1f 40 00          	nopl   0x0(%rax)

0000000000401030 <clone@plt>:
  401030:	ff 25 ca 2f 00 00    	jmp    *0x2fca(%rip)        # 404000 <clone@GLIBC_2.2.5>
  401036:	68 00 00 00 00       	push   $0x0
  40103b:	e9 e0 ff ff ff       	jmp    401020 <_init+0x20>

0000000000401040 <getpid@plt>:
  401040:	ff 25 c2 2f 00 00    	jmp    *0x2fc2(%rip)        # 404008 <getpid@GLIBC_2.2.5>
  401046:	68 01 00 00 00       	push   $0x1
  40104b:	e9 d0 ff ff ff       	jmp    401020 <_init+0x20>

0000000000401050 <__stack_chk_fail@plt>:
  401050:	ff 25 ba 2f 00 00    	jmp    *0x2fba(%rip)        # 404010 <__stack_chk_fail@GLIBC_2.4>
  401056:	68 02 00 00 00       	push   $0x2
  40105b:	e9 c0 ff ff ff       	jmp    401020 <_init+0x20>

0000000000401060 <mmap@plt>:
  401060:	ff 25 b2 2f 00 00    	jmp    *0x2fb2(%rip)        # 404018 <mmap@GLIBC_2.2.5>
  401066:	68 03 00 00 00       	push   $0x3
  40106b:	e9 b0 ff ff ff       	jmp    401020 <_init+0x20>

0000000000401070 <printf@plt>:
  401070:	ff 25 aa 2f 00 00    	jmp    *0x2faa(%rip)        # 404020 <printf@GLIBC_2.2.5>
  401076:	68 04 00 00 00       	push   $0x4
  40107b:	e9 a0 ff ff ff       	jmp    401020 <_init+0x20>

0000000000401080 <ftruncate@plt>:
  401080:	ff 25 a2 2f 00 00    	jmp    *0x2fa2(%rip)        # 404028 <ftruncate@GLIBC_2.2.5>
  401086:	68 05 00 00 00       	push   $0x5
  40108b:	e9 90 ff ff ff       	jmp    401020 <_init+0x20>

0000000000401090 <close@plt>:
  401090:	ff 25 9a 2f 00 00    	jmp    *0x2f9a(%rip)        # 404030 <close@GLIBC_2.2.5>
  401096:	68 06 00 00 00       	push   $0x6
  40109b:	e9 80 ff ff ff       	jmp    401020 <_init+0x20>

00000000004010a0 <open@plt>:
  4010a0:	ff 25 92 2f 00 00    	jmp    *0x2f92(%rip)        # 404038 <open@GLIBC_2.2.5>
  4010a6:	68 07 00 00 00       	push   $0x7
  4010ab:	e9 70 ff ff ff       	jmp    401020 <_init+0x20>

00000000004010b0 <perror@plt>:
  4010b0:	ff 25 8a 2f 00 00    	jmp    *0x2f8a(%rip)        # 404040 <perror@GLIBC_2.2.5>
  4010b6:	68 08 00 00 00       	push   $0x8
  4010bb:	e9 60 ff ff ff       	jmp    401020 <_init+0x20>

00000000004010c0 <sleep@plt>:
  4010c0:	ff 25 82 2f 00 00    	jmp    *0x2f82(%rip)        # 404048 <sleep@GLIBC_2.2.5>
  4010c6:	68 09 00 00 00       	push   $0x9
  4010cb:	e9 50 ff ff ff       	jmp    401020 <_init+0x20>

Disassembly of section .text:

00000000004010d0 <_start>:
  4010d0:	f3 0f 1e fa          	endbr64
  4010d4:	31 ed                	xor    %ebp,%ebp
  4010d6:	49 89 d1             	mov    %rdx,%r9
  4010d9:	5e                   	pop    %rsi
  4010da:	48 89 e2             	mov    %rsp,%rdx
  4010dd:	48 83 e4 f0          	and    $0xfffffffffffffff0,%rsp
  4010e1:	50                   	push   %rax
  4010e2:	54                   	push   %rsp
  4010e3:	45 31 c0             	xor    %r8d,%r8d
  4010e6:	31 c9                	xor    %ecx,%ecx
  4010e8:	48 c7 c7 e4 12 40 00 	mov    $0x4012e4,%rdi
  4010ef:	ff 15 d3 2e 00 00    	call   *0x2ed3(%rip)        # 403fc8 <__libc_start_main@GLIBC_2.34>
  4010f5:	f4                   	hlt
  4010f6:	66 2e 0f 1f 84 00 00 	cs nopw 0x0(%rax,%rax,1)
  4010fd:	00 00 00 

0000000000401100 <_dl_relocate_static_pie>:
  401100:	f3 0f 1e fa          	endbr64
  401104:	c3                   	ret
  401105:	66 2e 0f 1f 84 00 00 	cs nopw 0x0(%rax,%rax,1)
  40110c:	00 00 00 
  40110f:	90                   	nop

0000000000401110 <deregister_tm_clones>:
  401110:	b8 60 40 40 00       	mov    $0x404060,%eax
  401115:	48 3d 60 40 40 00    	cmp    $0x404060,%rax
  40111b:	74 13                	je     401130 <deregister_tm_clones+0x20>
  40111d:	48 8b 05 ac 2e 00 00 	mov    0x2eac(%rip),%rax        # 403fd0 <_ITM_deregisterTMCloneTable>
  401124:	48 85 c0             	test   %rax,%rax
  401127:	74 07                	je     401130 <deregister_tm_clones+0x20>
  401129:	bf 60 40 40 00       	mov    $0x404060,%edi
  40112e:	ff e0                	jmp    *%rax
  401130:	c3                   	ret
  401131:	0f 1f 40 00          	nopl   0x0(%rax)
  401135:	66 66 2e 0f 1f 84 00 	data16 cs nopw 0x0(%rax,%rax,1)
  40113c:	00 00 00 00 

0000000000401140 <register_tm_clones>:
  401140:	be 60 40 40 00       	mov    $0x404060,%esi
  401145:	48 81 ee 60 40 40 00 	sub    $0x404060,%rsi
  40114c:	48 89 f0             	mov    %rsi,%rax
  40114f:	48 c1 ee 3f          	shr    $0x3f,%rsi
  401153:	48 c1 f8 03          	sar    $0x3,%rax
  401157:	48 01 c6             	add    %rax,%rsi
  40115a:	48 d1 fe             	sar    $1,%rsi
  40115d:	74 19                	je     401178 <register_tm_clones+0x38>
  40115f:	48 8b 05 7a 2e 00 00 	mov    0x2e7a(%rip),%rax        # 403fe0 <_ITM_registerTMCloneTable>
  401166:	48 85 c0             	test   %rax,%rax
  401169:	74 0d                	je     401178 <register_tm_clones+0x38>
  40116b:	bf 60 40 40 00       	mov    $0x404060,%edi
  401170:	ff e0                	jmp    *%rax
  401172:	66 0f 1f 44 00 00    	nopw   0x0(%rax,%rax,1)
  401178:	c3                   	ret
  401179:	0f 1f 80 00 00 00 00 	nopl   0x0(%rax)

0000000000401180 <__do_global_dtors_aux>:
  401180:	f3 0f 1e fa          	endbr64
  401184:	80 3d d5 2e 00 00 00 	cmpb   $0x0,0x2ed5(%rip)        # 404060 <__TMC_END__>
  40118b:	75 13                	jne    4011a0 <__do_global_dtors_aux+0x20>
  40118d:	55                   	push   %rbp
  40118e:	48 89 e5             	mov    %rsp,%rbp
  401191:	e8 7a ff ff ff       	call   401110 <deregister_tm_clones>
  401196:	c6 05 c3 2e 00 00 01 	movb   $0x1,0x2ec3(%rip)        # 404060 <__TMC_END__>
  40119d:	5d                   	pop    %rbp
  40119e:	c3                   	ret
  40119f:	90                   	nop
  4011a0:	c3                   	ret
  4011a1:	0f 1f 40 00          	nopl   0x0(%rax)
  4011a5:	66 66 2e 0f 1f 84 00 	data16 cs nopw 0x0(%rax,%rax,1)
  4011ac:	00 00 00 00 

00000000004011b0 <frame_dummy>:
  4011b0:	f3 0f 1e fa          	endbr64
  4011b4:	eb 8a                	jmp    401140 <register_tm_clones>

00000000004011b6 <recursive_hello>:
#define SLEEP_SECS 0

// do not use static var for depth
// because it will be placed in .data section
// but we want to see it in stack
void recursive_hello(long long depth) {
  4011b6:	55                   	push   %rbp
  4011b7:	48 89 e5             	mov    %rsp,%rbp
  4011ba:	48 83 ec 30          	sub    $0x30,%rsp
  4011be:	48 89 7d d8          	mov    %rdi,-0x28(%rbp)
  4011c2:	64 48 8b 04 25 28 00 	mov    %fs:0x28,%rax
  4011c9:	00 00 
  4011cb:	48 89 45 f8          	mov    %rax,-0x8(%rbp)
  4011cf:	31 c0                	xor    %eax,%eax
    char array[] = "hello world";
  4011d1:	48 b8 68 65 6c 6c 6f 	movabs $0x6f77206f6c6c6568,%rax
  4011d8:	20 77 6f 
  4011db:	48 89 45 ec          	mov    %rax,-0x14(%rbp)
  4011df:	c7 45 f4 72 6c 64 00 	movl   $0x646c72,-0xc(%rbp)
    sleep(SLEEP_SECS);
  4011e6:	bf 00 00 00 00       	mov    $0x0,%edi
  4011eb:	e8 d0 fe ff ff       	call   4010c0 <sleep@plt>
    if (depth < MAX_DEPTH) {
  4011f0:	48 83 7d d8 09       	cmpq   $0x9,-0x28(%rbp)
  4011f5:	7f 10                	jg     401207 <recursive_hello+0x51>
        recursive_hello(depth + 1);
  4011f7:	48 8b 45 d8          	mov    -0x28(%rbp),%rax
  4011fb:	48 83 c0 01          	add    $0x1,%rax
  4011ff:	48 89 c7             	mov    %rax,%rdi
  401202:	e8 af ff ff ff       	call   4011b6 <recursive_hello>
    }

}
  401207:	90                   	nop
  401208:	48 8b 45 f8          	mov    -0x8(%rbp),%rax
  40120c:	64 48 2b 04 25 28 00 	sub    %fs:0x28,%rax
  401213:	00 00 
  401215:	74 05                	je     40121c <recursive_hello+0x66>
  401217:	e8 34 fe ff ff       	call   401050 <__stack_chk_fail@plt>
  40121c:	c9                   	leave
  40121d:	c3                   	ret

000000000040121e <child_main>:

int child_main(void *arg) {
  40121e:	55                   	push   %rbp
  40121f:	48 89 e5             	mov    %rsp,%rbp
  401222:	48 83 ec 10          	sub    $0x10,%rsp
  401226:	48 89 7d f8          	mov    %rdi,-0x8(%rbp)
    recursive_hello(0);
  40122a:	bf 00 00 00 00       	mov    $0x0,%edi
  40122f:	e8 82 ff ff ff       	call   4011b6 <recursive_hello>
}
  401234:	90                   	nop
  401235:	c9                   	leave
  401236:	c3                   	ret

0000000000401237 <map_stack>:

void *map_stack(const char *fname) {
  401237:	55                   	push   %rbp
  401238:	48 89 e5             	mov    %rsp,%rbp
  40123b:	48 83 ec 20          	sub    $0x20,%rsp
  40123f:	48 89 7d e8          	mov    %rdi,-0x18(%rbp)
    
    int fd = open(fname, O_CREAT | O_RDWR  | O_TRUNC | O_DSYNC);
  401243:	48 8b 45 e8          	mov    -0x18(%rbp),%rax
  401247:	be 42 12 00 00       	mov    $0x1242,%esi
  40124c:	48 89 c7             	mov    %rax,%rdi
  40124f:	b8 00 00 00 00       	mov    $0x0,%eax
  401254:	e8 47 fe ff ff       	call   4010a0 <open@plt>
  401259:	89 45 f4             	mov    %eax,-0xc(%rbp)
    if (fd == -1) {
  40125c:	83 7d f4 ff          	cmpl   $0xffffffff,-0xc(%rbp)
  401260:	75 13                	jne    401275 <map_stack+0x3e>
        perror(fname);
  401262:	48 8b 45 e8          	mov    -0x18(%rbp),%rax
  401266:	48 89 c7             	mov    %rax,%rdi
  401269:	e8 42 fe ff ff       	call   4010b0 <perror@plt>
        return NULL;
  40126e:	b8 00 00 00 00       	mov    $0x0,%eax
  401273:	eb 6d                	jmp    4012e2 <map_stack+0xab>
    }

    if (ftruncate(fd, STACK_SIZE) == -1) {
  401275:	8b 45 f4             	mov    -0xc(%rbp),%eax
  401278:	be 00 28 00 00       	mov    $0x2800,%esi
  40127d:	89 c7                	mov    %eax,%edi
  40127f:	e8 fc fd ff ff       	call   401080 <ftruncate@plt>
  401284:	83 f8 ff             	cmp    $0xffffffff,%eax
  401287:	75 11                	jne    40129a <map_stack+0x63>
        perror("ftruncate");
  401289:	bf 04 20 40 00       	mov    $0x402004,%edi
  40128e:	e8 1d fe ff ff       	call   4010b0 <perror@plt>
        return NULL;
  401293:	b8 00 00 00 00       	mov    $0x0,%eax
  401298:	eb 48                	jmp    4012e2 <map_stack+0xab>
    } 
    
    void *map_addr = mmap(NULL, STACK_SIZE, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
  40129a:	8b 45 f4             	mov    -0xc(%rbp),%eax
  40129d:	41 b9 00 00 00 00    	mov    $0x0,%r9d
  4012a3:	41 89 c0             	mov    %eax,%r8d
  4012a6:	b9 01 00 00 00       	mov    $0x1,%ecx
  4012ab:	ba 03 00 00 00       	mov    $0x3,%edx
  4012b0:	be 00 28 00 00       	mov    $0x2800,%esi
  4012b5:	bf 00 00 00 00       	mov    $0x0,%edi
  4012ba:	e8 a1 fd ff ff       	call   401060 <mmap@plt>
  4012bf:	48 89 45 f8          	mov    %rax,-0x8(%rbp)
    if (map_addr == MAP_FAILED) {
  4012c3:	48 83 7d f8 ff       	cmpq   $0xffffffffffffffff,-0x8(%rbp)
  4012c8:	75 0a                	jne    4012d4 <map_stack+0x9d>
        perror("mmap");
  4012ca:	bf 0e 20 40 00       	mov    $0x40200e,%edi
  4012cf:	e8 dc fd ff ff       	call   4010b0 <perror@plt>
    }
    close(fd);
  4012d4:	8b 45 f4             	mov    -0xc(%rbp),%eax
  4012d7:	89 c7                	mov    %eax,%edi
  4012d9:	e8 b2 fd ff ff       	call   401090 <close@plt>
    return map_addr;
  4012de:	48 8b 45 f8          	mov    -0x8(%rbp),%rax
}
  4012e2:	c9                   	leave
  4012e3:	c3                   	ret

00000000004012e4 <main>:

int main() {
  4012e4:	55                   	push   %rbp
  4012e5:	48 89 e5             	mov    %rsp,%rbp
  4012e8:	48 83 ec 20          	sub    $0x20,%rsp
    void *const stack = map_stack("stack.bin");
  4012ec:	bf 13 20 40 00       	mov    $0x402013,%edi
  4012f1:	e8 41 ff ff ff       	call   401237 <map_stack>
  4012f6:	48 89 45 f0          	mov    %rax,-0x10(%rbp)
    if (!stack) {
  4012fa:	48 83 7d f0 00       	cmpq   $0x0,-0x10(%rbp)
  4012ff:	75 0a                	jne    40130b <main+0x27>
        return 1;
  401301:	b8 01 00 00 00       	mov    $0x1,%eax
  401306:	e9 81 00 00 00       	jmp    40138c <main+0xa8>
    }  
    sleep(2);
  40130b:	bf 02 00 00 00       	mov    $0x2,%edi
  401310:	e8 ab fd ff ff       	call   4010c0 <sleep@plt>
    void *const stack_top = stack + STACK_SIZE;
  401315:	48 8b 45 f0          	mov    -0x10(%rbp),%rax
  401319:	48 05 00 28 00 00    	add    $0x2800,%rax
  40131f:	48 89 45 f8          	mov    %rax,-0x8(%rbp)

    pid_t pid = clone(child_main, stack_top, 0, NULL);
  401323:	48 8b 45 f8          	mov    -0x8(%rbp),%rax
  401327:	b9 00 00 00 00       	mov    $0x0,%ecx
  40132c:	ba 00 00 00 00       	mov    $0x0,%edx
  401331:	48 89 c6             	mov    %rax,%rsi
  401334:	bf 1e 12 40 00       	mov    $0x40121e,%edi
  401339:	b8 00 00 00 00       	mov    $0x0,%eax
  40133e:	e8 ed fc ff ff       	call   401030 <clone@plt>
  401343:	89 45 ec             	mov    %eax,-0x14(%rbp)
    if (pid == -1) {
  401346:	83 7d ec ff          	cmpl   $0xffffffff,-0x14(%rbp)
  40134a:	75 11                	jne    40135d <main+0x79>
        perror("clone");
  40134c:	bf 1d 20 40 00       	mov    $0x40201d,%edi
  401351:	e8 5a fd ff ff       	call   4010b0 <perror@plt>
        return 1;
  401356:	b8 01 00 00 00       	mov    $0x1,%eax
  40135b:	eb 2f                	jmp    40138c <main+0xa8>
    }
    printf("parent pid = %d\n", getpid());
  40135d:	e8 de fc ff ff       	call   401040 <getpid@plt>
  401362:	89 c6                	mov    %eax,%esi
  401364:	bf 23 20 40 00       	mov    $0x402023,%edi
  401369:	b8 00 00 00 00       	mov    $0x0,%eax
  40136e:	e8 fd fc ff ff       	call   401070 <printf@plt>
    printf("child pid= %d\n", pid);
  401373:	8b 45 ec             	mov    -0x14(%rbp),%eax
  401376:	89 c6                	mov    %eax,%esi
  401378:	bf 34 20 40 00       	mov    $0x402034,%edi
  40137d:	b8 00 00 00 00       	mov    $0x0,%eax
  401382:	e8 e9 fc ff ff       	call   401070 <printf@plt>
    //munmap(stack, STACK_SIZE);
    return 0;
  401387:	b8 00 00 00 00       	mov    $0x0,%eax
  40138c:	c9                   	leave
  40138d:	c3                   	ret

Disassembly of section .fini:

0000000000401390 <_fini>:
  401390:	f3 0f 1e fa          	endbr64
  401394:	48 83 ec 08          	sub    $0x8,%rsp
  401398:	48 83 c4 08          	add    $0x8,%rsp
  40139c:	c3                   	ret
