	.file	"hello_static.c"
	.text
	.globl	__stack_chk_fail
	.type	__stack_chk_fail, @function
__stack_chk_fail:
.LFB0:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	nop
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE0:
	.size	__stack_chk_fail, .-__stack_chk_fail
	.globl	exit
	.type	exit, @function
exit:
.LFB1:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl	%edi, -20(%rbp)
	movl	$60, %eax
	movl	-20(%rbp), %edx
	movl	%edx, %edi
#APP
# 7 "hello_static.c" 1
		syscall

# 0 "" 2
#NO_APP
	movq	%rax, -8(%rbp)
	ud2
	.cfi_endproc
.LFE1:
	.size	exit, .-exit
	.globl	my_write
	.type	my_write, @function
my_write:
.LFB2:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	movl	%edi, -20(%rbp)
	movq	%rsi, -32(%rbp)
	movq	%rdx, -40(%rbp)
	movl	$1, %eax
	movl	$1, %edi
	movq	-32(%rbp), %rsi
	movq	-40(%rbp), %rdx
#APP
# 24 "hello_static.c" 1
		syscall

# 0 "" 2
#NO_APP
	movq	%rax, -8(%rbp)
	cmpq	$-4095, -8(%rbp)
	jl	.L4
	cmpq	$0, -8(%rbp)
	jns	.L4
	movq	$-1, %rax
	jmp	.L5
.L4:
	movq	-8(%rbp), %rax
.L5:
	popq	%rbp
	.cfi_def_cfa 7, 8
	ret
	.cfi_endproc
.LFE2:
	.size	my_write, .-my_write
	.globl	_start
	.type	_start, @function
_start:
.LFB3:
	.cfi_startproc
	pushq	%rbp
	.cfi_def_cfa_offset 16
	.cfi_offset 6, -16
	movq	%rsp, %rbp
	.cfi_def_cfa_register 6
	subq	$64, %rsp
	movq	%fs:40, %rax
	movq	%rax, -8(%rbp)
	xorl	%eax, %eax
	movabsq	$7310584035479091565, %rax
	movabsq	$3201897072897302586, %rdx
	movq	%rax, -48(%rbp)
	movq	%rdx, -40(%rbp)
	movabsq	$2318350419654699040, %rax
	movabsq	$2851464966991735, %rdx
	movq	%rax, -39(%rbp)
	movq	%rdx, -31(%rbp)
	leaq	-48(%rbp), %rax
	movl	$25, %edx
	movq	%rax, %rsi
	movl	$1, %edi
	call	my_write
	movq	%rax, -56(%rbp)
	movl	$0, %edi
	call	exit
	.cfi_endproc
.LFE3:
	.size	_start, .-_start
	.ident	"GCC: (GNU) 15.2.1 20260209"
	.section	.note.GNU-stack,"",@progbits
