package ronan_hanley.ip_notify;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(
            names = {"--expected-ip", "-e"},
            description = "Expected/current IP address"
    )
    String expectedIp;

    @Parameter(
            names = {"--sleep-time", "-s"},
            description = "Time to sleep after checking the IP address before trying again (minutes)"
    )
    String sleepTime;

    @Parameter(
            names = {"--retry-timeout", "-r"},
            description = "Time to sleep failing to send the notification email before trying again"
    )
    String retryTimeout;

    @Parameter(
            names = {"--user", "-u"},
            description = "Username of the SMTP email to use to send the notification emails from"
    )
    String username;

    @Parameter(
            names = {"--pass", "-p"},
            description = "Password for the SMTP email to use to send the notification emails from",
            password = true
    )
    String password;

    @Parameter(
            names = {"--check-site", "-c"},
            description = "Site to use to check the current IP address"
    )
    String ipCheckSite = "http://www.icanhazip.com/";
}
