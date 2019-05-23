package ronan_hanley.ip_notify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class IPNotify {
    private boolean running;
    private static final String checkSite = "http://www.icanhazip.com/";
    private static final int retryTimeout = 10;

    public static void main(String[] args) {
        new IPNotify().go(args[0], Integer.parseInt(args[1]));
    }

    public void go(String expectedIP, int sleepTime) {
        URL checkSiteURL = null;
        String currentIP = null;
        boolean gotIP;
        boolean ipMatches;

        try {
            checkSiteURL = new URL(checkSite);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        running = true;
        while (running) {
            System.out.printf("Checking current external IP address on %s...%n", checkSite);

            gotIP = false;
            do {
                try {
                    currentIP = getCurrentIP(checkSiteURL);
                    gotIP = true;
                } catch (IOException e) {
                    System.out.printf("Error in retrieving current IP. Retrying in %d seconds...%n", retryTimeout);

                    try {
                        Thread.sleep(retryTimeout * 1000);
                    } catch (InterruptedException e2) {
                        e.printStackTrace();
                    }
                }
            } while (!gotIP);

            ipMatches = currentIP.equals(expectedIP);
            System.out.printf("Expected IP: %s - Actual IP: %s - Matches? %b%n", expectedIP, currentIP, ipMatches);

            if (ipMatches) {
                System.out.printf("IP has not changed. Sleeping for %d minutes before trying again...%n", sleepTime);

                try {
                    Thread.sleep(sleepTime * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("~~~ WARNING: IP has changed! ~~~");
                System.out.println("Sending notification email...");
                EmailBot.sendIPChangeNotification(expectedIP, currentIP, retryTimeout);

                System.out.printf("Sleeping for %d seconds before exiting...%n", retryTimeout);

                try {
                    Thread.sleep(retryTimeout * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                System.out.println("Exiting...");
                System.exit(0);
            }
        }
    }

    private String getCurrentIP(URL checkSiteURL) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(checkSiteURL.openStream()));
        String ip = in.readLine();
        in.close();
        return ip;
    }

}
