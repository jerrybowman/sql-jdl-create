package com.jerrybowman.jhipster.jdl;

import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
public class TestCommands {

    @ShellMethod("Add two integers together.")
    public int addTwoNumbers(int a, int b) {
        return a + b;
    }
}