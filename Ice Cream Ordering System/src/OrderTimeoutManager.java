import java.util.concurrent.*;
import javax.swing.*;

public class OrderTimeoutManager {

    private static final int START_TIMEOUT_SECONDS = 300;   // 5 min
    private static final int ACTIVITY_TIMEOUT_SECONDS = 180; // 3 min

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> pendingTimeout;
    private final Runnable onTimeout;

    public OrderTimeoutManager(Runnable onTimeout) {
        this.onTimeout = onTimeout;
    }

    /** Call when a new order is started. */
    public void onOrderStarted() {
        schedule(START_TIMEOUT_SECONDS);
    }

    /** Call when an item is added to the cart or checkout is opened. */
    public void onOrderActivity() {
        schedule(ACTIVITY_TIMEOUT_SECONDS);
    }

    /** Call when the order completes or is cancelled — stops the timer. */
    public void onOrderEnded() {
        cancel();
    }

    public void shutdown() {
        cancel();
        scheduler.shutdown();
    }

    private void schedule(int delaySeconds) {
        cancel(); // Cancel any existing timeout before scheduling a new one
        pendingTimeout = scheduler.schedule(
                () -> SwingUtilities.invokeLater(onTimeout),
                delaySeconds,
                TimeUnit.SECONDS
        );
    }

    private void cancel() {
        if (pendingTimeout != null && !pendingTimeout.isDone()) {
            pendingTimeout.cancel(false);
        }
    }
}