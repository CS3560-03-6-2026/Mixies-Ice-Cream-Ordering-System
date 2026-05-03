package Mixie.Kiosk;
import javax.swing.*;
import java.awt.*;

/**
 * KioskNavigator is responsible for switching between kiosk screens.
 * Each panel calls these methods instead of directly manipulating the CardLayout
 */
public class KioskNavigator {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    public KioskNavigator(CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
    }

    public void showWelcome() {
        cardLayout.show(cardPanel, "welcome");
    }

    public void showMenu() {
        cardLayout.show(cardPanel, "menu");
    }

    public void showCustomize() {
        cardLayout.show(cardPanel, "customize");
    }

    public void showCart() {
        cardLayout.show(cardPanel, "cart");
    }

    public void showCheckout() {
        cardLayout.show(cardPanel, "checkout");
    }
}