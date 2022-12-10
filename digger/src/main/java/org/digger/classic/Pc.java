package org.digger.classic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;

class Pc {

    Digger dig;

    Image[] image = new Image[2];
    Image currentImage;

    MemoryImageSource[] source = new MemoryImageSource[2];
    MemoryImageSource currentSource;
    int[] pixels;
    int width = 320, height = 200, size = width * height;
    byte[][][] pal = {

            {{0, (byte) 0x00, (byte) 0xAA, (byte) 0xAA}, {0, (byte) 0xAA, (byte) 0x00, (byte) 0x54},
                    {0, (byte) 0x00, (byte) 0x00, (byte) 0x00}},

            {{0, (byte) 0x54, (byte) 0xFF, (byte) 0xFF}, {0, (byte) 0xFF, (byte) 0x54, (byte) 0xFF},
                    {0, (byte) 0x54, (byte) 0x54, (byte) 0x54}}};

    Pc(Digger d) {
        dig = d;
    }

    void gclear() {
        for (int i = 0; i < size; i++)
            pixels[i] = 0;
        currentSource.newPixels();
    }

    long gethrt() {
        return System.currentTimeMillis();
    }

    int getkips() {
        return 0; // phony
    }

    void ggeti(int x, int y, short[] p, int w, int h) {

        int src = 0;
        int dest = y * width + (x & 0xfffc);

        for (int i = 0; i < h; i++) {
            int d = dest;
            for (int j = 0; j < w; j++) {
                p[src++] = (short) ((((((pixels[d] << 2) | pixels[d + 1]) << 2) | pixels[d + 2]) << 2) | pixels[d + 3]);
                d += 4;
                if (src == p.length)
                    return;
            }
            dest += width;
        }

    }

    int ggetpix(int x, int y) {
        int ofs = width * y + x & 0xfffc;
        return (((((pixels[ofs] << 2) | pixels[ofs + 1]) << 2) | pixels[ofs + 2]) << 2) | pixels[ofs + 3];
    }

    void ginit() {
    }

    void ginten(int inten) {
        // TODO switchPallete !
        currentSource = source[inten & 1];
        currentImage = image[inten & 1];
        currentSource.newPixels();
    }

    void gpal(int pal) {
    }

    void gputi(int x, int y, short[] p, int w, int h) {
        gputi(x, y, p, w, h, true);
    }

    void gputi(int x, int y, short[] p, int w, int h, boolean b) {

        int src = 0;
        int dest = y * width + (x & 0xfffc);

        for (int i = 0; i < h; i++) {
            int d = dest;
            for (int j = 0; j < w; j++) {
                short px = p[src++];
                pixels[d + 3] = px & 3;
                px >>= 2;
                pixels[d + 2] = px & 3;
                px >>= 2;
                pixels[d + 1] = px & 3;
                px >>= 2;
                pixels[d] = px & 3;
                d += 4;
                if (src == p.length) {
                    return;
                }
            }
            dest += width;
        }

    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    void gputim(int x, int y, int ch, int w, int h) {

        short[] spr = cgagrafx.cgatable[ch * 2];
        short[] msk = cgagrafx.cgatable[ch * 2 + 1];
        //65532 1111111111111100
        int src = 0;
        int dest = y * width + (x & 0xfffc);

        for (int i = 0; i < h; i++) {
            int d = dest;
            for (int j = 0; j < w; j++) {
                short px = spr[src];
                short mx = msk[src];
                src++;
                if ((mx & 3) == 0)
                    pixels[d + 3] = px & 3;
                px >>= 2;
                if ((mx & (3 << 2)) == 0)
                    pixels[d + 2] = px & 3;
                px >>= 2;
                if ((mx & (3 << 4)) == 0)
                    pixels[d + 1] = px & 3;
                px >>= 2;
                if ((mx & (3 << 6)) == 0)
                    pixels[d] = px & 3;
                d += 4;
                if (src == spr.length || src == msk.length) {

                    MemoryImageSource memoryImageSource = new MemoryImageSource(width, height,
                            new IndexColorModel(8, 4, pal[0][0], pal[0][1], pal[0][2]),
                            pixels, 0, width);
                    Image image = Toolkit.getDefaultToolkit().createImage(memoryImageSource);
                    BufferedImage bufferedImage = toBufferedImage(image);
                    try {
                        BufferedImage cropped = bufferedImage.getSubimage(x, y, 30, h);
                        try {
                            ImageIO.write(cropped, "png",
                                    new File("out/" + ch * 2 + ".png"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception ex) {

                    }


                    return;
                }
            }
            dest += width;
        }

    }

    void gtitle() {

        int src = 0, dest = 0, plus = 0;

        while (true) {

            if (src >= cgagrafx.cgatitledat.length)
                break;

            int b = cgagrafx.cgatitledat[src++], l, c;

            if (b == 0xfe) {
                l = cgagrafx.cgatitledat[src++];
                if (l == 0)
                    l = 256;
                c = cgagrafx.cgatitledat[src++];
            } else {
                l = 1;
                c = b;
            }

            for (int i = 0; i < l; i++) {
                int px = c, adst = 0;
                if (dest < 32768)
                    adst = (dest / 320) * 640 + dest % 320;
                else
                    adst = 320 + ((dest - 32768) / 320) * 640 + (dest - 32768) % 320;
                pixels[adst + 3] = px & 3;
                px >>= 2;
                pixels[adst + 2] = px & 3;
                px >>= 2;
                pixels[adst + 1] = px & 3;
                px >>= 2;
                pixels[adst] = px & 3;
                dest += 4;
                if (dest >= 65535)
                    break;
            }

            if (dest >= 65535)
                break;

        }

    }

    void gwrite(int x, int y, int ch, int c) {
        gwrite(x, y, ch, c, false);
    }

    void gwrite(int x, int y, int ch, int c, boolean upd) {

        int dest = x + y * width, ofs = 0, color = c & 3;

        ch -= 32;
        if ((ch < 0) || (ch > 0x5f))
            return;

        short[] chartab = alpha.ascii2cga[ch];

        if (chartab == null)
            return;

        for (int i = 0; i < 12; i++) {
            int d = dest;
            for (int j = 0; j < 3; j++) {
                int px = chartab[ofs++];
                pixels[d + 3] = px & color;
                px >>= 2;
                pixels[d + 2] = px & color;
                px >>= 2;
                pixels[d + 1] = px & color;
                px >>= 2;
                pixels[d] = px & color;
                d += 4;
            }
            dest += width;
        }

        if (upd)
            currentSource.newPixels(x, y, 12, 12);

    }


}
