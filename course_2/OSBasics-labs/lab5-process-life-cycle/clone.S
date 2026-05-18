	.file	"clone.c"
	.text
.Ltext0:
	.file 0 "/home/kapiuser/Documents/university-repo/course_2/OSBasics-labs/lab5-process-life-cycle" "clone.c"
	.globl	recursive_hello
	.type	recursive_hello, @function
recursive_hello:
.LFB0:
	.file 1 "clone.c"
	.loc 1 17 39
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$48, %rsp
	movq	%rdi, -40(%rbp)
	.loc 1 17 39
	movq	%fs:40, %rax
	movq	%rax, -8(%rbp)
	xorl	%eax, %eax
	.loc 1 18 10
	movabsq	$8031924123371070824, %rax
	movq	%rax, -20(%rbp)
	movl	$6581362, -12(%rbp)
	.loc 1 19 5
	movl	$0, %edi
	call	sleep@PLT
	.loc 1 20 8
	cmpq	$9, -40(%rbp)
	jg	.L4
	.loc 1 21 9
	movq	-40(%rbp), %rax
	addq	$1, %rax
	movq	%rax, %rdi
	call	recursive_hello
.L4:
	.loc 1 24 1
	nop
	movq	-8(%rbp), %rax
	subq	%fs:40, %rax
	je	.L3
	call	__stack_chk_fail@PLT
.L3:
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE0:
	.size	recursive_hello, .-recursive_hello
	.globl	child_main
	.type	child_main, @function
child_main:
.LFB1:
	.loc 1 26 27
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$16, %rsp
	movq	%rdi, -8(%rbp)
	.loc 1 27 5
	movl	$0, %edi
	call	recursive_hello
	.loc 1 28 1
	nop
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE1:
	.size	child_main, .-child_main
	.section	.rodata
.LC0:
	.string	"ftruncate"
.LC1:
	.string	"mmap"
	.text
	.globl	map_stack
	.type	map_stack, @function
map_stack:
.LFB2:
	.loc 1 30 36
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$32, %rsp
	movq	%rdi, -24(%rbp)
	.loc 1 32 14
	movq	-24(%rbp), %rax
	movl	$4674, %esi
	movq	%rax, %rdi
	movl	$0, %eax
	call	open@PLT
	movl	%eax, -12(%rbp)
	.loc 1 33 8
	cmpl	$-1, -12(%rbp)
	jne	.L7
	.loc 1 34 9
	movq	-24(%rbp), %rax
	movq	%rax, %rdi
	call	perror@PLT
	.loc 1 35 16
	movl	$0, %eax
	jmp	.L8
.L7:
	.loc 1 38 9
	movl	-12(%rbp), %eax
	movl	$10240, %esi
	movl	%eax, %edi
	call	ftruncate@PLT
	.loc 1 38 8 discriminator 1
	cmpl	$-1, %eax
	jne	.L9
	.loc 1 39 9
	leaq	.LC0(%rip), %rax
	movq	%rax, %rdi
	call	perror@PLT
	.loc 1 40 16
	movl	$0, %eax
	jmp	.L8
.L9:
	.loc 1 43 22
	movl	-12(%rbp), %eax
	movl	$0, %r9d
	movl	%eax, %r8d
	movl	$1, %ecx
	movl	$3, %edx
	movl	$10240, %esi
	movl	$0, %edi
	call	mmap@PLT
	movq	%rax, -8(%rbp)
	.loc 1 44 8
	cmpq	$-1, -8(%rbp)
	jne	.L10
	.loc 1 45 9
	leaq	.LC1(%rip), %rax
	movq	%rax, %rdi
	call	perror@PLT
.L10:
	.loc 1 47 5
	movl	-12(%rbp), %eax
	movl	%eax, %edi
	call	close@PLT
	.loc 1 48 12
	movq	-8(%rbp), %rax
.L8:
	.loc 1 49 1
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE2:
	.size	map_stack, .-map_stack
	.section	.rodata
.LC2:
	.string	"stack.bin"
.LC3:
	.string	"clone"
.LC4:
	.string	"parent pid = %d\n"
.LC5:
	.string	"child pid= %d\n"
	.text
	.globl	main
	.type	main, @function
main:
.LFB3:
	.loc 1 51 12
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$32, %rsp
	.loc 1 52 25
	leaq	.LC2(%rip), %rax
	movq	%rax, %rdi
	call	map_stack
	movq	%rax, -16(%rbp)
	.loc 1 53 8
	cmpq	$0, -16(%rbp)
	jne	.L12
	.loc 1 54 16
	movl	$1, %eax
	jmp	.L13
.L12:
	.loc 1 56 5
	movl	$2, %edi
	call	sleep@PLT
	.loc 1 57 17
	movq	-16(%rbp), %rax
	addq	$10240, %rax
	movq	%rax, -8(%rbp)
	.loc 1 59 17
	movq	-8(%rbp), %rax
	leaq	child_main(%rip), %rdi
	movl	$0, %ecx
	movl	$0, %edx
	movq	%rax, %rsi
	movl	$0, %eax
	call	clone@PLT
	movl	%eax, -20(%rbp)
	.loc 1 60 8
	cmpl	$-1, -20(%rbp)
	jne	.L14
	.loc 1 61 9
	leaq	.LC3(%rip), %rax
	movq	%rax, %rdi
	call	perror@PLT
	.loc 1 62 16
	movl	$1, %eax
	jmp	.L13
.L14:
	.loc 1 64 5
	call	getpid@PLT
	movl	%eax, %edx
	.loc 1 64 5 is_stmt 0 discriminator 1
	leaq	.LC4(%rip), %rax
	movl	%edx, %esi
	movq	%rax, %rdi
	movl	$0, %eax
	call	printf@PLT
	.loc 1 65 5 is_stmt 1
	movl	-20(%rbp), %eax
	leaq	.LC5(%rip), %rdx
	movl	%eax, %esi
	movq	%rdx, %rdi
	movl	$0, %eax
	call	printf@PLT
	.loc 1 67 12
	movl	$0, %eax
.L13:
	.loc 1 68 1
	leave
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE3:
	.size	main, .-main
.Letext0:
	.file 2 "/usr/lib/gcc/x86_64-pc-linux-gnu/16.1.1/include/stddef.h"
	.file 3 "/usr/include/bits/types.h"
	.file 4 "/usr/include/unistd.h"
	.file 5 "/usr/include/stdio.h"
	.file 6 "/usr/include/bits/sched.h"
	.file 7 "/usr/include/sys/mman.h"
	.file 8 "/usr/include/fcntl.h"
	.section	.debug_info,"",@progbits
.Ldebug_info0:
	.long	0x2e3
	.value	0x5
	.byte	0x1
	.byte	0x8
	.long	.Ldebug_abbrev0
	.uleb128 0xd
	.long	.LASF34
	.byte	0x1d
	.byte	0x3
	.long	0x31647
	.long	.LASF0
	.long	.LASF1
	.quad	.Ltext0
	.quad	.Letext0-.Ltext0
	.long	.Ldebug_line0
	.uleb128 0x2
	.byte	0x8
	.byte	0x7
	.long	.LASF2
	.uleb128 0x2
	.byte	0x4
	.byte	0x7
	.long	.LASF3
	.uleb128 0xe
	.byte	0x8
	.uleb128 0x9
	.long	0x41
	.uleb128 0x5
	.long	.LASF9
	.byte	0x2
	.byte	0xe5
	.byte	0x17
	.long	0x33
	.uleb128 0x2
	.byte	0x1
	.byte	0x8
	.long	.LASF4
	.uleb128 0x2
	.byte	0x2
	.byte	0x7
	.long	.LASF5
	.uleb128 0x2
	.byte	0x1
	.byte	0x6
	.long	.LASF6
	.uleb128 0x2
	.byte	0x2
	.byte	0x5
	.long	.LASF7
	.uleb128 0xf
	.byte	0x4
	.byte	0x5
	.string	"int"
	.uleb128 0x2
	.byte	0x8
	.byte	0x5
	.long	.LASF8
	.uleb128 0x5
	.long	.LASF10
	.byte	0x3
	.byte	0x98
	.byte	0x19
	.long	0x77
	.uleb128 0x5
	.long	.LASF11
	.byte	0x3
	.byte	0x9a
	.byte	0x19
	.long	0x70
	.uleb128 0x2
	.byte	0x1
	.byte	0x6
	.long	.LASF12
	.uleb128 0x9
	.long	0x96
	.uleb128 0xa
	.long	0x9d
	.uleb128 0x10
	.long	0x70
	.long	0xb6
	.uleb128 0x1
	.long	0x41
	.byte	0
	.uleb128 0x11
	.long	.LASF13
	.byte	0x4
	.value	0x104
	.byte	0x11
	.long	0x8a
	.uleb128 0x2
	.byte	0x8
	.byte	0x5
	.long	.LASF14
	.uleb128 0x2
	.byte	0x8
	.byte	0x7
	.long	.LASF15
	.uleb128 0x2
	.byte	0x10
	.byte	0x5
	.long	.LASF16
	.uleb128 0x2
	.byte	0x10
	.byte	0x7
	.long	.LASF17
	.uleb128 0x3
	.long	.LASF18
	.byte	0x5
	.value	0x172
	.byte	0xc
	.long	0x70
	.long	0xf7
	.uleb128 0x1
	.long	0xa2
	.uleb128 0x6
	.byte	0
	.uleb128 0x12
	.long	.LASF35
	.byte	0x4
	.value	0x28a
	.byte	0x10
	.long	0x8a
	.uleb128 0x7
	.long	.LASF19
	.byte	0x6
	.byte	0x84
	.byte	0xc
	.long	0x70
	.long	0x12a
	.uleb128 0x1
	.long	0x12a
	.uleb128 0x1
	.long	0x41
	.uleb128 0x1
	.long	0x70
	.uleb128 0x1
	.long	0x41
	.uleb128 0x6
	.byte	0
	.uleb128 0xa
	.long	0xa7
	.uleb128 0x3
	.long	.LASF20
	.byte	0x4
	.value	0x166
	.byte	0xc
	.long	0x70
	.long	0x146
	.uleb128 0x1
	.long	0x70
	.byte	0
	.uleb128 0x7
	.long	.LASF21
	.byte	0x7
	.byte	0x39
	.byte	0xe
	.long	0x41
	.long	0x175
	.uleb128 0x1
	.long	0x41
	.uleb128 0x1
	.long	0x48
	.uleb128 0x1
	.long	0x70
	.uleb128 0x1
	.long	0x70
	.uleb128 0x1
	.long	0x70
	.uleb128 0x1
	.long	0x7e
	.byte	0
	.uleb128 0x3
	.long	.LASF22
	.byte	0x4
	.value	0x419
	.byte	0xc
	.long	0x70
	.long	0x191
	.uleb128 0x1
	.long	0x70
	.uleb128 0x1
	.long	0x7e
	.byte	0
	.uleb128 0x13
	.long	.LASF36
	.byte	0x5
	.value	0x368
	.byte	0xd
	.long	0x1a4
	.uleb128 0x1
	.long	0xa2
	.byte	0
	.uleb128 0x7
	.long	.LASF23
	.byte	0x8
	.byte	0xd1
	.byte	0xc
	.long	0x70
	.long	0x1c0
	.uleb128 0x1
	.long	0xa2
	.uleb128 0x1
	.long	0x70
	.uleb128 0x6
	.byte	0
	.uleb128 0x3
	.long	.LASF24
	.byte	0x4
	.value	0x1d0
	.byte	0x15
	.long	0x3a
	.long	0x1d7
	.uleb128 0x1
	.long	0x3a
	.byte	0
	.uleb128 0x8
	.long	.LASF27
	.byte	0x33
	.byte	0x5
	.long	0x70
	.quad	.LFB3
	.quad	.LFE3-.LFB3
	.uleb128 0x1
	.byte	0x9c
	.long	0x223
	.uleb128 0x4
	.long	.LASF25
	.byte	0x34
	.byte	0x11
	.long	0x43
	.uleb128 0x2
	.byte	0x91
	.sleb128 -32
	.uleb128 0x4
	.long	.LASF26
	.byte	0x39
	.byte	0x11
	.long	0x43
	.uleb128 0x2
	.byte	0x91
	.sleb128 -24
	.uleb128 0xb
	.string	"pid"
	.byte	0x3b
	.byte	0xb
	.long	0xb6
	.uleb128 0x2
	.byte	0x91
	.sleb128 -36
	.byte	0
	.uleb128 0x8
	.long	.LASF28
	.byte	0x1e
	.byte	0x7
	.long	0x41
	.quad	.LFB2
	.quad	.LFE2-.LFB2
	.uleb128 0x1
	.byte	0x9c
	.long	0x26e
	.uleb128 0xc
	.long	.LASF31
	.byte	0x1e
	.byte	0x1d
	.long	0xa2
	.uleb128 0x2
	.byte	0x91
	.sleb128 -40
	.uleb128 0xb
	.string	"fd"
	.byte	0x20
	.byte	0x9
	.long	0x70
	.uleb128 0x2
	.byte	0x91
	.sleb128 -28
	.uleb128 0x4
	.long	.LASF29
	.byte	0x2b
	.byte	0xb
	.long	0x41
	.uleb128 0x2
	.byte	0x91
	.sleb128 -24
	.byte	0
	.uleb128 0x8
	.long	.LASF30
	.byte	0x1a
	.byte	0x5
	.long	0x70
	.quad	.LFB1
	.quad	.LFE1-.LFB1
	.uleb128 0x1
	.byte	0x9c
	.long	0x29f
	.uleb128 0x14
	.string	"arg"
	.byte	0x1
	.byte	0x1a
	.byte	0x16
	.long	0x41
	.uleb128 0x2
	.byte	0x91
	.sleb128 -24
	.byte	0
	.uleb128 0x15
	.long	.LASF37
	.byte	0x1
	.byte	0x11
	.byte	0x6
	.quad	.LFB0
	.quad	.LFE0-.LFB0
	.uleb128 0x1
	.byte	0x9c
	.long	0x2da
	.uleb128 0xc
	.long	.LASF32
	.byte	0x11
	.byte	0x20
	.long	0xc3
	.uleb128 0x2
	.byte	0x91
	.sleb128 -56
	.uleb128 0x4
	.long	.LASF33
	.byte	0x12
	.byte	0xa
	.long	0x2da
	.uleb128 0x2
	.byte	0x91
	.sleb128 -36
	.byte	0
	.uleb128 0x16
	.long	0x96
	.uleb128 0x17
	.long	0x33
	.byte	0xb
	.byte	0
	.byte	0
	.section	.debug_abbrev,"",@progbits
.Ldebug_abbrev0:
	.uleb128 0x1
	.uleb128 0x5
	.byte	0
	.uleb128 0x49
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x2
	.uleb128 0x24
	.byte	0
	.uleb128 0xb
	.uleb128 0xb
	.uleb128 0x3e
	.uleb128 0xb
	.uleb128 0x3
	.uleb128 0xe
	.byte	0
	.byte	0
	.uleb128 0x3
	.uleb128 0x2e
	.byte	0x1
	.uleb128 0x3f
	.uleb128 0x19
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0x5
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x27
	.uleb128 0x19
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x3c
	.uleb128 0x19
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x4
	.uleb128 0x34
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0x21
	.sleb128 1
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2
	.uleb128 0x18
	.byte	0
	.byte	0
	.uleb128 0x5
	.uleb128 0x16
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x6
	.uleb128 0x18
	.byte	0
	.byte	0
	.byte	0
	.uleb128 0x7
	.uleb128 0x2e
	.byte	0x1
	.uleb128 0x3f
	.uleb128 0x19
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x27
	.uleb128 0x19
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x3c
	.uleb128 0x19
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x8
	.uleb128 0x2e
	.byte	0x1
	.uleb128 0x3f
	.uleb128 0x19
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0x21
	.sleb128 1
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x27
	.uleb128 0x19
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x11
	.uleb128 0x1
	.uleb128 0x12
	.uleb128 0x7
	.uleb128 0x40
	.uleb128 0x18
	.uleb128 0x7c
	.uleb128 0x19
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x9
	.uleb128 0x26
	.byte	0
	.uleb128 0x49
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0xa
	.uleb128 0xf
	.byte	0
	.uleb128 0xb
	.uleb128 0x21
	.sleb128 8
	.uleb128 0x49
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0xb
	.uleb128 0x34
	.byte	0
	.uleb128 0x3
	.uleb128 0x8
	.uleb128 0x3a
	.uleb128 0x21
	.sleb128 1
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2
	.uleb128 0x18
	.byte	0
	.byte	0
	.uleb128 0xc
	.uleb128 0x5
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0x21
	.sleb128 1
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2
	.uleb128 0x18
	.byte	0
	.byte	0
	.uleb128 0xd
	.uleb128 0x11
	.byte	0x1
	.uleb128 0x25
	.uleb128 0xe
	.uleb128 0x13
	.uleb128 0xb
	.uleb128 0x90
	.uleb128 0xb
	.uleb128 0x91
	.uleb128 0x6
	.uleb128 0x3
	.uleb128 0x1f
	.uleb128 0x1b
	.uleb128 0x1f
	.uleb128 0x11
	.uleb128 0x1
	.uleb128 0x12
	.uleb128 0x7
	.uleb128 0x10
	.uleb128 0x17
	.byte	0
	.byte	0
	.uleb128 0xe
	.uleb128 0xf
	.byte	0
	.uleb128 0xb
	.uleb128 0xb
	.byte	0
	.byte	0
	.uleb128 0xf
	.uleb128 0x24
	.byte	0
	.uleb128 0xb
	.uleb128 0xb
	.uleb128 0x3e
	.uleb128 0xb
	.uleb128 0x3
	.uleb128 0x8
	.byte	0
	.byte	0
	.uleb128 0x10
	.uleb128 0x15
	.byte	0x1
	.uleb128 0x27
	.uleb128 0x19
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x11
	.uleb128 0x16
	.byte	0
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0x5
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x12
	.uleb128 0x2e
	.byte	0
	.uleb128 0x3f
	.uleb128 0x19
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0x5
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x27
	.uleb128 0x19
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x3c
	.uleb128 0x19
	.byte	0
	.byte	0
	.uleb128 0x13
	.uleb128 0x2e
	.byte	0x1
	.uleb128 0x3f
	.uleb128 0x19
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0x5
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x27
	.uleb128 0x19
	.uleb128 0x3c
	.uleb128 0x19
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x14
	.uleb128 0x5
	.byte	0
	.uleb128 0x3
	.uleb128 0x8
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2
	.uleb128 0x18
	.byte	0
	.byte	0
	.uleb128 0x15
	.uleb128 0x2e
	.byte	0x1
	.uleb128 0x3f
	.uleb128 0x19
	.uleb128 0x3
	.uleb128 0xe
	.uleb128 0x3a
	.uleb128 0xb
	.uleb128 0x3b
	.uleb128 0xb
	.uleb128 0x39
	.uleb128 0xb
	.uleb128 0x27
	.uleb128 0x19
	.uleb128 0x11
	.uleb128 0x1
	.uleb128 0x12
	.uleb128 0x7
	.uleb128 0x40
	.uleb128 0x18
	.uleb128 0x7c
	.uleb128 0x19
	.uleb128 0x1
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x16
	.uleb128 0x1
	.byte	0x1
	.uleb128 0x49
	.uleb128 0x13
	.byte	0
	.byte	0
	.uleb128 0x17
	.uleb128 0x21
	.byte	0
	.uleb128 0x49
	.uleb128 0x13
	.uleb128 0x2f
	.uleb128 0xb
	.byte	0
	.byte	0
	.byte	0
	.section	.debug_aranges,"",@progbits
	.long	0x2c
	.value	0x2
	.long	.Ldebug_info0
	.byte	0x8
	.byte	0
	.value	0
	.value	0
	.quad	.Ltext0
	.quad	.Letext0-.Ltext0
	.quad	0
	.quad	0
	.section	.debug_line,"",@progbits
.Ldebug_line0:
	.section	.debug_str,"MS",@progbits,1
.LASF35:
	.string	"getpid"
.LASF17:
	.string	"__int128 unsigned"
.LASF9:
	.string	"size_t"
.LASF31:
	.string	"fname"
.LASF34:
	.string	"GNU C23 16.1.1 20260430 -mtune=generic -march=x86-64 -g"
.LASF25:
	.string	"stack"
.LASF13:
	.string	"pid_t"
.LASF26:
	.string	"stack_top"
.LASF19:
	.string	"clone"
.LASF6:
	.string	"signed char"
.LASF4:
	.string	"unsigned char"
.LASF36:
	.string	"perror"
.LASF20:
	.string	"close"
.LASF5:
	.string	"short unsigned int"
.LASF16:
	.string	"__int128"
.LASF33:
	.string	"array"
.LASF37:
	.string	"recursive_hello"
.LASF21:
	.string	"mmap"
.LASF27:
	.string	"main"
.LASF30:
	.string	"child_main"
.LASF2:
	.string	"long unsigned int"
.LASF3:
	.string	"unsigned int"
.LASF15:
	.string	"long long unsigned int"
.LASF10:
	.string	"__off_t"
.LASF14:
	.string	"long long int"
.LASF32:
	.string	"depth"
.LASF12:
	.string	"char"
.LASF18:
	.string	"printf"
.LASF23:
	.string	"open"
.LASF7:
	.string	"short int"
.LASF22:
	.string	"ftruncate"
.LASF8:
	.string	"long int"
.LASF11:
	.string	"__pid_t"
.LASF28:
	.string	"map_stack"
.LASF29:
	.string	"map_addr"
.LASF24:
	.string	"sleep"
	.section	.debug_line_str,"MS",@progbits,1
.LASF0:
	.string	"clone.c"
.LASF1:
	.string	"/home/kapiuser/Documents/university-repo/course_2/OSBasics-labs/lab5-process-life-cycle"
	.ident	"GCC: (GNU) 16.1.1 20260430"
	.section	.note.GNU-stack,"",@progbits
