  .text
  .global add_asm

add_asm:
    movq %rdi, %rax
    addq %rsi, %rax
    ret
