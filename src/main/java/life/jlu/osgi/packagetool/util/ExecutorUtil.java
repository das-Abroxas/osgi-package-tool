package life.jlu.osgi.packagetool.util;

import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.*;


/**
 * Util class with static methods to create different
 */
public class ExecutorUtil {

    private ExecutorUtil() {}


    /**
     * Creates an ExecutorService that starts a single thread as daemon.
     * @return Single thread ExecutorService
     */
    public static ExecutorService getSingleThreadDaemonExecutor() {

        return Executors.newSingleThreadExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Creates an ExecutorService that starts a scheduled threads as daemon.
     * @return Scheduled thread ExecutorService
     */
    public static ScheduledExecutorService getScheduledThreadDaemonExecutor() {

        return Executors.newScheduledThreadPool(1, r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Runs a task enveloped in a SingleThreadExecutor so to not block the applications main thread.
     * The task will be terminated after a 30 second timeout.
     *
     * @param task Task to be executed
     */
    public static void runNonBlockingTimeoutExecutor(Runnable task) {

        ExecutorService bg = getSingleThreadDaemonExecutor();

        bg.execute(() -> {

            Callable<List<String>> callable = Executors.callable(task, null);
            FutureTask<List<String>> future = new FutureTask<>(callable);
            Thread thread = new Thread(future);
            thread.start();

            try {
                future.get(30, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                future.cancel(true);

                Platform.runLater(() ->
                        DialogUtil.showErrorDialog("Interruption Error",
                                "OSGi Container Distro creation was interrupted."));
            } catch (ExecutionException e) {
                System.out.println("----- ExecutionException -----");

                Platform.runLater(() ->
                        DialogUtil.showErrorDialog("Execution Error", "Something went really wrong ..."));
            } catch (TimeoutException e) {
                future.cancel(true);

                Platform.runLater(() ->
                        DialogUtil.showErrorDialog("Timeout Error",
                                "OSGi Container Distro creation took longer than specified timeout."));
            }
        });
    }

    /**
     * Runs a task enveloped in a SingleThreadExecutor so to not block the applications main thread.
     * The task will be terminated after the specified timeout in seconds.
     *
     * @param task Task to be executed
     * @param timeout Timeout in seconds
     */
    public static void runNonBlockingTimeoutExecutor(Runnable task, int timeout) {

        ExecutorService bg = getSingleThreadDaemonExecutor();

        bg.execute(() -> {
            Callable<List<String>> callable = Executors.callable(task, null);

            FutureTask<List<String>> future = new FutureTask<>(callable);
            Thread thread = new Thread(future);
            thread.start();

            try {
                future.get(timeout, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                future.cancel(true);

                Platform.runLater(() ->
                        DialogUtil.showErrorDialog("Interruption Error",
                                "OSGi Container Distro creation was interrupted."));
            } catch (ExecutionException e) {
                System.out.println("----- ExecutionException -----");

                Platform.runLater(() ->
                        DialogUtil.showErrorDialog("Execution Error", "Something went really wrong ..."));
            } catch (TimeoutException e) {
                future.cancel(true);

                Platform.runLater(() ->
                        DialogUtil.showErrorDialog("Timeout Error",
                                "OSGi Container Distro creation took longer than specified timeout."));
            }
        });
    }
}
