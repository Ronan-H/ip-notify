package ronan_hanley.ip_notify;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(
            names = {"--expected-ip", "-e"},
            description = "Expected/current IP address",
            required = true
    )
    String expectedIp;

    @Parameter(
            names = {"--user", "-u"},
            description = "Address of the SMTP email to use to send the notification emails from",
            required = true
    )
    String username;

    @Parameter(
            names = {"--pass", "-p"},
            description = "Password for the SMTP email to use to send the notification emails from",
            password = true,
            required = true
    )
    String password;

    @Parameter(
            names = {"--recipient", "-rec"},
            description = "Recipient address of the notification emails",
            required = true
    )
    String recipient;

    @Parameter(
            names = {"--check-site", "-c"},
            description = "Site to use to check the current IP address"
    )
    String ipCheckSite = "https://icanhazip.com/";

    @Parameter(
            names = {"--sleep-time", "-s"},
            description = "Time to sleep after checking the IP address before trying again (minutes)"
    )
    Integer sleepTime = 10;

    @Parameter(
            names = {"--retry-timeout", "-rt"},
            description = "Time to sleep failing to send the notification email before trying again"
    )
    Integer retryTimeout = 5;
}
