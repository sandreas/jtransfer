
package de.fynder.jtransfer;

public class Main {
    private static Transfer app;
    private static Settings settings = new Settings();

    public static void main(String[] args) {
        try {
            runApplication(args);
        } catch (Error e) {
            handleError(e);
        } catch (Exception e) {
            handleUncaughtException(e);
        }

        System.exit(app.getStatusCode());
    }

    private static void runApplication(String[] args) throws Exception {
        app = new Transfer();
        app.initSettings(settings);
        app.run(args);

    }

    private static void handleError(Error e) {
        System.out.print("error: ");
        System.out.println(e.getMessage());

        if(settings.debug) {
            e.printStackTrace();
        }
    }

    private static void handleUncaughtException(Exception e) {
        System.out.println(e.getMessage());
        if(settings.debug) {
            e.printStackTrace();
        }
    }
}