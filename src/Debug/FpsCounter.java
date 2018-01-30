package Debug;


public class FpsCounter {

    public long lastTime; //System time in milliseconds.
    public int framesCounted = 0;
    public int updatesCounted = 0;

    private boolean render = false;
    private int amountToRenderFrames = 0;
    private int amountToRenderUpdates = 0;

    public FpsCounter() {
        lastTime = System.currentTimeMillis();
    }

    // Sets last time for FPS counter.
    public void setTime(long curTime) {
        lastTime = curTime;
    }

    public void update() {
        updatesCounted++;
        if (checkTime()) {
            amountToRenderFrames = framesCounted;
            amountToRenderUpdates = updatesCounted;
            framesCounted = 0;
            updatesCounted = 0;
            render = true;
        }
    }

    public void render() {
        framesCounted++;
        if (render) {
            render = false;
            System.out.println("UPS: " + amountToRenderUpdates + " | FPS: " + amountToRenderFrames);
        }
    }

    //Check if 1 second passed.
    public boolean checkTime() {
        long newTime = System.currentTimeMillis();
        if (newTime - lastTime >= 1000) {
            lastTime += 1000;
            return true;
        }
        return false;
    }

    public void reset() {
        framesCounted = 0;
    }



}
