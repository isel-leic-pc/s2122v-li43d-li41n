  .text
  .global context_switch

# X86-64 calling conventions
# https://en.wikipedia.org/wiki/X86_calling_conventions

context_switch:
    pushq %rbp
	pushq %rbx
	pushq %r12
	pushq %r13
	pushq %r14
	pushq %r15    
    
    movq %rsp, (%rdi)
    movq (%rsi), %rsp

    pop %r15
    pop %r14
    pop %r13
	pop %r12
	pop %rbx
    pop %rbp
    ret
