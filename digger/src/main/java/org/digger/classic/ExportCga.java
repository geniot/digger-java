package org.digger.classic;

import org.apache.commons.lang3.StringUtils;

public class ExportCga {

    public static void main(String[] args) {
        for (int i = 0; i < cgagrafx.cgaemerald.length; i++) {
            String binaryString = Integer.toBinaryString(cgagrafx.cgardigger1[i]);
            String padded = StringUtils.rightPad(binaryString, 16, '0');
            System.out.print(padded);
            if (i > 0 && (i + 1) % 4 == 0) {
                System.out.print("\n");
            }
        }
    }
}
