package com.egyptianbanks.ipn.uberridesmergetool.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ScrapRidesHandler {
    public static void handleScrapRides(Component parent) {
        JTextArea statusTextArea = null;
        if (parent instanceof JFrame) {
            // Try to find the statusTextArea in MainScreen
            for (Component c : ((JFrame) parent).getContentPane().getComponents()) {
                if (c instanceof JPanel) {
                    for (Component cc : ((JPanel) c).getComponents()) {
                        if (cc instanceof JScrollPane) {
                            JScrollPane scroll = (JScrollPane) cc;
                            JViewport viewport = scroll.getViewport();
                            if (viewport.getView() instanceof JTextArea) {
                                statusTextArea = (JTextArea) viewport.getView();
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (statusTextArea == null) {
            // fallback: show dialog only
            statusTextArea = new JTextArea();
        }
        appendStatus(statusTextArea, "Starting Uber rides scraping...");

        try {
            JOptionPane.showMessageDialog(parent,
                    "1. Please log in to https://riders.uber.com/trips in your browser.\n" +
                    "2. Open browser dev tools, go to Application/Storage > Cookies, and copy the value of the 'sid' and 'jwt-session' cookies.\n" +
                    "3. Paste the cookies in the next dialog (format: sid=...; jwt-session=...)\n\n" +
                    "If you do not see 'jwt-session', use the cookie named 'jwt-session' (not '_ua' or 'uber-session').",
                    "Manual Cookie Required", JOptionPane.INFORMATION_MESSAGE);

            String cookieInput = JOptionPane.showInputDialog(parent, "Paste your cookies here (e.g. sid=...; jwt-session=...):");
            if (cookieInput == null || cookieInput.trim().isEmpty()) {
                appendStatus(statusTextArea, "No cookies provided. Cannot proceed.");
                JOptionPane.showMessageDialog(parent, "No cookies provided. Cannot proceed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            appendStatus(statusTextArea, "Connecting to Uber trips page...");
            String url = "https://riders.uber.com/trips";
            Connection connection = Jsoup.connect(url)
                    .header("Cookie", cookieInput)
                    .userAgent("Mozilla/5.0")
                    .timeout(15000)
                    .method(Connection.Method.GET);

            Document doc = connection.get();

            appendStatus(statusTextArea, "Fetched trips page. Checking login status...");
            String bodyText = doc.body().text().toLowerCase();
            if (doc.title().toLowerCase().contains("login") || bodyText.contains("sign in") || bodyText.contains("log in") || bodyText.contains("تسجيل الدخول")) {
                appendStatus(statusTextArea, "Login failed. Please ensure your cookies are correct and not expired.");
                JOptionPane.showMessageDialog(parent, "Login failed. Please ensure your cookies are correct and not expired.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            appendStatus(statusTextArea, "Parsing rides for the current month...");
            String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(new Date());
            int ridesFound = 0, ridesDownloaded = 0;
            List<String> rideLinks = new ArrayList<>();

            Elements rideCards = doc.select("[data-testid='trip-card'], .trip-card, .trips-card");

            for (Element card : rideCards) {
                String cardText = card.text().toLowerCase();
                if (cardText.contains("cancelled") || cardText.contains("unfulfilled") || cardText.contains("ملغاة")) {
                    appendStatus(statusTextArea, "Skipping cancelled/unfulfilled ride.");
                    continue;
                }
                String dateText = "";
                Element dateElem = card.selectFirst("[data-testid='trip-date']");
                if (dateElem != null) {
                    dateText = dateElem.text();
                } else {
                    dateText = card.text();
                }
                if (!dateText.startsWith(currentMonth)) {
                    appendStatus(statusTextArea, "Skipping ride not in current month: " + dateText);
                    continue;
                }

                Element linkElem = card.selectFirst("a[href]");
                if (linkElem != null) {
                    String rideHref = linkElem.absUrl("href");
                    if (!rideHref.isEmpty()) {
                        rideLinks.add(rideHref);
                        appendStatus(statusTextArea, "Found ride link: " + rideHref);
                    }
                }
            }

            appendStatus(statusTextArea, "Total ride links found for current month: " + rideLinks.size());

            for (String rideUrl : rideLinks) {
                appendStatus(statusTextArea, "Fetching ride: " + rideUrl);
                Connection rideConn = Jsoup.connect(rideUrl)
                        .header("Cookie", cookieInput)
                        .userAgent("Mozilla/5.0")
                        .timeout(15000)
                        .method(Connection.Method.GET);
                Document rideDoc = rideConn.get();
                String rideHtml = rideDoc.outerHtml();
                if (rideHtml.contains("N Teseen")) {
                    ridesDownloaded++;
                    String fileName = "uber_ride_" + ridesDownloaded + ".html";
                    try (FileWriter writer = new FileWriter(fileName)) {
                        writer.write(rideHtml);
                    }
                    appendStatus(statusTextArea, "Saved ride with 'N Teseen' to: " + fileName);
                } else {
                    appendStatus(statusTextArea, "Ride does not contain 'N Teseen'.");
                }
                ridesFound++;
            }

            String doneMsg = "Done!\nTotal rides found for this month: " + ridesFound + "\nRides with 'N Teseen': " + ridesDownloaded;
            appendStatus(statusTextArea, doneMsg);
            JOptionPane.showMessageDialog(parent, doneMsg, "Scraping Complete", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            ex.printStackTrace();
            appendStatus(statusTextArea, "Failed to download rides: " + ex.getMessage());
            JOptionPane.showMessageDialog(parent, "Failed to download rides: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void appendStatus(JTextArea statusTextArea, String text) {
        SwingUtilities.invokeLater(() -> {
            if (!statusTextArea.getText().isEmpty()) {
                statusTextArea.append("\n");
            }
            statusTextArea.append("[ScrapRides] " + text);
            statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
        });
    }
}
