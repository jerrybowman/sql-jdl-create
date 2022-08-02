# Running the application.

The database connection assumes that the connection will be
using integrated authorization, not user/password. In order for this 
to work under Windows, the `mssql-jdbc_auth-10.2.1.x64.dll` must be copied 
to a directory in the PATH. Assuming this directory is in your PATH:
`C:\Users\%username%\AppData\Local\Microsoft\WindowsApps` (where %username% is 
your username) copy this file there. If that folder does not exit, use
some folder that makes sense that is in your PATH.

Then the easiest thing to do is run:

    mvnw spring-boot:run

At the `shell:>` prompt you can type `help` to see a list of command. To get
the information on a specific command, `help <command>`.

