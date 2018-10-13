package engine;

import engine.graph.Input;
import engine.interfaces.IGameLogic;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 120;
    public static final int TARGET_UPS = 30;
    private final Window window;
    private final Thread gameLoopThread;
    private static Timer timer;
    private final IGameLogic gameLogic;
    private final Input input;
    private double lastFps;
    private int fps;
    private static int displayFPS;
    private String windowTitle;

    public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this(windowTitle, 0, 0, vSync, opts, gameLogic);
    }

    public GameEngine(String windowTitle, int width, int height, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this.windowTitle = windowTitle;
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowTitle, width, height, vSync, opts);
        input = new Input(window);
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    public GameEngine() {
        this.window = null;
        this.gameLoopThread = null;
        timer = new Timer();
        this.gameLogic = null;
        this.input = null;
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        input.init(window);
        gameLogic.init(window);
        lastFps = timer.getTime();
        fps = 0;
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if ( !window.isvSync() ) {
                sync();
            }
        }
    }
    
    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    protected void input() {
        input.input(window);
        gameLogic.input(window, input);
    }

    protected void update(float interval) {
        gameLogic.update(interval, input);
        input.update(window);
    }

    protected void render() {
        if ( window.getWindowOptions().showFps && timer.getLastLoopTime() - lastFps > 1 ) {
            lastFps = timer.getLastLoopTime();
            window.setWindowTitle(windowTitle + " - " + fps + " FPS");
            displayFPS = fps;
            fps = 0;
        }
        fps++;
        gameLogic.render(window);
        window.update();
    }
    
    public static int getFPS() {
    	return displayFPS;
    }

    public static Timer getTimer() {
    	return timer;
    }

    protected void cleanup() {
        gameLogic.cleanUp();                
    }
}
