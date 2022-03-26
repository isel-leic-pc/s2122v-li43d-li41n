
    .extern running_uthread
    .extern cleanup_uthread

    .text
    .global context_switch
    .global internal_exit

# https://en.wikipedia.org/wiki/X86_calling_conventions

# rdi - pointer for the "switching out" uthread
# rsi - pointer for the "switching in" uthread
context_switch:
	
    pushq %rbp
	pushq %rbx
	pushq %r12
	pushq %r13
	pushq %r14
	pushq %r15

	movq %rsp, (%rdi)
	
    movq %rsi, running_uthread(%rip)
	
    movq (%rsi), %rsp
	
	popq %r15
	popq %r14
	popq %r13
	popq %r12
	popq %rbx
	popq %rbp
	
	ret

# rdi - pointer for the "switching in" uthread
internal_exit:

    movq running_uthread(%rip), %rsi
    movq %rdi, running_uthread(%rip)

    movq (%rdi), %rsp

    movq %rsi, %rdi
    call cleanup_uthread
	
	popq %r15
	popq %r14
	popq %r13
	popq %r12
	popq %rbx
	popq %rbp
	
	ret
