package sample;


public class WindowThread extends Thread {
    Window mainWindow;

    public WindowThread(){}

    private String threadName;

    WindowThread(String threadName) {
        super(threadName);
        this.threadName = threadName;
    }

    @Override
    public void run() {
        mainWindow = new Window();
        mainWindow.launchWindow();
    }
}
