package com.ui;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class Sample {
	public static void main (String args[]) {
		PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
		if (printService != null) {
		    DocFlavor[] supportedFlavors = printService.getSupportedDocFlavors();
		    for (DocFlavor flavor : supportedFlavors) {
		        System.out.println("Supported flavor: " + flavor);
		    }
		} else {
		    System.out.println("No default print service found.");
		}

	}

}
