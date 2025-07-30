package com.f1.pdf;

import java.io.File;
import java.io.IOException;

import com.f1.utils.IOH;
import com.f1.utils.structs.table.BasicTable;

public class PdfTest {

	public static final String DEST = "/home/share/temp/hello";

	public static void main(String a[]) throws IOException {
		PdfBuilder pdf = new PdfBuilder();
		pdf.setPageMargin(.25f, .25f, .25f, .25f);
		pdf.addCornerIcon(0, IOH.readData(new File("/home/share/temp/new_logo.png")), "plan", "https://3forge.com", (byte) 0, .1f, null);
		pdf.addCornerIcon(0, IOH.readData(new File("/home/share/temp/new_logo.png")), "asdf", "https://3forge.com", (byte) 1, .2f, null);
		pdf.addCornerIcon(0, IOH.readData(new File("/home/share/temp/new_logo.png")), null, "https://3forge.com", (byte) 2, .3f, null);
		pdf.addCornerIcon(0, IOH.readData(new File("/home/share/temp/new_logo.png")), null, "https://3forge.com", (byte) 3, .4f, null);
		pdf.addCornerIcon(0, IOH.readData(new File("/home/share/temp/new_logo.png")), null, "https://3forge.com", (byte) 4, .5f, null);
		pdf.addCornerIcon(0, IOH.readData(new File("/home/share/temp/new_logo.png")), null, "https://3forge.com", (byte) 5, .6f, null);
		pdf.setMarginBelowHeader(.1f);
		pdf.setMarginAboveFooter(.1f);
		pdf.start();
		BasicTable bt = new BasicTable(new String[] { "Test", "This", "Out", "with,", "some", "values", "a", "b", "c", "d", "e" });
		for (int n = 0; n < 20; n++)
			bt.getRows().addRow(n, 8, 9, 4, 5, 6, 1, 2, 3, 4, 5);
		pdf.setHorizontalAlignment("CENTER");
		pdf.appendTable(bt);
		pdf.setFont("5");
		pdf.setHorizontalAlignment("LEFT");
		pdf.appendImage(IOH.readData(new File("/home/share/temp/new_logo.png")), 2);
		pdf.setHorizontalAlignment("CENTER");
		pdf.appendImage(IOH.readData(new File("/home/share/temp/new_logo.png")), 2);
		pdf.setHorizontalAlignment("RIGHT");
		pdf.appendImage(IOH.readData(new File("/home/share/temp/new_logo.png")), 2);
		pdf.appendText("testme");
		pdf.setFont("10");
		pdf.appendPageBreak();
		pdf.appendText("testyou");
		pdf.setFont("10 #0000FF italic");
		pdf.appendText("This is a great table!");
		pdf.appendTable(bt);
		IOH.writeData(new File(DEST + ".pdf"), pdf.build());
	}
}
