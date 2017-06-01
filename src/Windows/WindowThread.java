package Windows;


public class WindowThread extends Thread {
    private Window mainWindow;

    public WindowThread(){}

    private String threadName;

    public WindowThread(String threadName) {
        super(threadName);
        this.threadName = threadName;
    }

    @Override
    public void run() {
        mainWindow = new Window();
        mainWindow.launchWindow();
    }
}
