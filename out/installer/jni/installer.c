#include <stdio.h>
#include <unistd.h>

int main(int argc, char** argv)
{
    int i = 0;
    while(i < 100)
    {
        printf("installer is running!---->pid:%d, ppid:%d, uid:%d, gid:%d\n", 
                   getpid(), getppid(), getuid(), getgid()); 
        sleep(1);
        i++;
    }
}