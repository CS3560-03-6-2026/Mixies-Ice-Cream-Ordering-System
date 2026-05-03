package Mixie.Kiosk;
/*
 * KioskSession stores all shared data for the kiosk.
 * Contains the state of the session that is shared across all kiosk panels.
 */

import Mixie.Flavor.IceCreamFlavor;

public class KioskSession {

    private int currentOrderID = -1;

    private IceCreamFlavor selectedFlavor;

    private double selectedTip = 0.0;

    public int getCurrentOrderID() {
        return currentOrderID;
    }

    public void setCurrentOrderID(int currentOrderID) {
        this.currentOrderID = currentOrderID;
    }

    public IceCreamFlavor getSelectedFlavor() {
        return selectedFlavor;
    }

    public void setSelectedFlavor(IceCreamFlavor selectedFlavor) {
        this.selectedFlavor = selectedFlavor;
    }

    public double getSelectedTip() {
        return selectedTip;
    }

    public void setSelectedTip(double selectedTip) {
        this.selectedTip = selectedTip;
    }

    // Reset is called after an order is completed so the kiosk is ready for the next customer.
    public void reset() {
        currentOrderID = -1;
        selectedFlavor = null;
        selectedTip = 0.0;
    }
}