package servidorso;
        
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class AnalizarImagen {
    
	public static String analizarImagen() throws Exception {
        File file = new File("/home/pi/NetBeansProjects/ServidorSo/imagen.jpg");
        ImageInputStream is = ImageIO.createImageInputStream(file);
        Iterator iter = ImageIO.getImageReaders(is);

        if (!iter.hasNext())
        {
            System.out.println("Cannot load the specified file "+ file);
            System.exit(1);
        }else{
        	System.out.println("Imagen encontrada ");
        }
        ImageReader imageReader = (ImageReader)iter.next();
        imageReader.setInput(is);

        BufferedImage image = imageReader.read(0);

        int height = image.getHeight()/2;
        int width = image.getWidth()/2;

        Map m = new HashMap();
        for(int i=0; i < width ; i=i+20)
        {
            for(int j=0; j < height ; j=j+20)
            {
                int rgb = image.getRGB(i, j);
                int[] rgbArr = getRGBArr(rgb);                
                // Filter out grays....                
                if (!isGray(rgbArr)) {                
                        Integer counter = (Integer) m.get(rgb);   
                        if (counter == null)
                            counter = 0;
                        counter++;                                
                        m.put(rgb, counter);                
                }               
            }
        }        
        String colourHex = getMostCommonColour(m);
        System.out.println(colourHex);
        return colourHex;
    }


    private static String getMostCommonColour(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, (Object o1, Object o2) -> ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue()));    
        Map.Entry me = (Map.Entry )list.get(list.size()-1);
        int[] rgb= getRGBArr((Integer)me.getKey());
        
        if (rgb[0] >= 100 && rgb[0] > rgb[1] && rgb[0] > rgb[2]){
        	System.out.println("Es rojo");
        }
        String r = Integer.toHexString(rgb[0]);
        String g = Integer.toHexString(rgb[1]);
        String b = Integer.toHexString(rgb[2]);
        
        if (r.length() < 2) r = "0" + r;
        if (g.length() < 2) g = "0" + g;
        if (b.length() < 2) b = "0" + b;
        
        return r+g+b;        
    }    

    private static int[] getRGBArr(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        return new int[]{red,green,blue};

  }
    private static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        // Filter out black, white and grays...... (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance) 
            if (rbDiff > tolerance || rbDiff < -tolerance) { 
                return false;
            }                 
        return true;
    }
}
