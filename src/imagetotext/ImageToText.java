/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Kevin Prehn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package imagetotext;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * A program that creates text files from images for levels in games.
 * @author Kevin Prehn
 */
public class ImageToText {

    public enum OutputFormat {JSON, KEYPAIRS};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        File outputFolder = new File(new File("").getAbsolutePath());
        File inputFolder = new File(new File("").getAbsolutePath());
        
        // handle command line arguments
        if (args.length % 2 == 0 && args.length > 0) {
            for (int i=0; i<args.length; i=i+2) {
                if (args[i].equals("-outf")) {
                    outputFolder = new File(new File(args[(i+1)]).getAbsolutePath());
                } else if (args[i].equals("-inf")) {
                    inputFolder = new File(new File(args[(i+1)]).getAbsolutePath());
                } else {
                    System.err.println("'" + args[i] + "' is not a valid argument");
                    System.err.println("Use '-outf [output folder]' or '-inf [input folder]'");
                    return;
                }
            }
        }
        
        System.out.println("Input directory: " + inputFolder.getAbsolutePath());
        System.out.println("Output directory: " + outputFolder.getAbsolutePath());
        
        // verify the folders are directories
        if (!inputFolder.isDirectory()) {
             System.err.println("The input folder must be a directory");
             return;
        }
        if (!outputFolder.isDirectory()) {
             System.err.println("The out folder must be a directory");
             return;
        }
        
        // outfile file parameters defaults
        float scale = 1;
        String seperator = " ";
        // Link a color in the input image to a keyword string for the output file
        HashMap<Color, String> keywordDefinitions = new HashMap<>();
        OutputFormat outputFormat = OutputFormat.KEYPAIRS;
        
        try {
            // read config file for the parameters
            File configFile = new File("config.txt");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            Files.newInputStream(configFile.toPath())));
            String input = "";
            while ((input = reader.readLine()) != null) {
                String[] tokens = input.split(" ");
                String k = tokens[0];
                if (k.equals("SCALE")) {
                    scale = Float.parseFloat(tokens[1]);
                } else if (k.equals("SEPERATOR")) {
                    seperator = tokens[1];
                } else if (k.equals("DEFINE")) {
                    String keyword = tokens[1];
                    Color color = new Color(
                        Integer.parseInt(tokens[2]),
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]));
                    keywordDefinitions.put(color, keyword);
                } else if (k.equals("OUTPUT_FORMAT")) {
                    if (tokens[1].equals("KEYPAIRS")) {
                        outputFormat = OutputFormat.KEYPAIRS;
                    } else if (tokens[1].equals("JSON")) {
                        outputFormat = OutputFormat.JSON;
                    }
                }
            }
        } catch(Exception e) {
            System.err.println("Error reading config file: " + e);
        }
        // finished reading config file
        
        // get all images in the input directory
        
        // for each image, read the pixel data and make blocks
        //  combine all the blocks to make smaller blocks
        //  write to a text file info about the blocks and the tags
        //  make the text file go into the output directory
        
        File[] inputFiles = inputFolder.listFiles();
        if (inputFiles.length == 0) {
            System.err.println("No input images found in the input directory");
            return;
        }
        //System.out.println("Found " + inputFiles.length + " files in directory");
        
        int imagesFound = 0;
        
        for (int i=0; i<inputFiles.length; i++) {
            File currentFile = inputFiles[i];
            //System.out.println(currentFile.getName());
            if (currentFile.canRead() && 
                    (currentFile.getName().endsWith(".png") ||
                     currentFile.getName().endsWith(".jpg") ||
                     currentFile.getName().endsWith(".gif"))) {
                // if the file was an image file
                imagesFound++;
            } else {
                continue;
            }
            
            
            try {
                BufferedImage img = (BufferedImage)ImageIO.read(currentFile);
                
                // first, convert all colors defined into rectangles
                ArrayList<TaggedRectangle> rectangles = new ArrayList<>();
                System.out.print("Reading " + currentFile.getName() + "...");
                for (int r=0; r<img.getHeight(); r++) {
                    for (int c=0; c<img.getWidth(); c++) {
                       Color pixelColor = new Color(img.getRGB(c, r));
                       String keyword = keywordDefinitions.get(pixelColor);
                       if (keyword != null) {
                           //System.out.println("Found a valid pixel at " + r + " " + c);
                           rectangles.add(new TaggedRectangle(keyword, c, r, 1, 1));
                       }
                    }
                }
                
                int rectanglesBefore = rectangles.size();
                
                int combinations = 0;
                
                do {
                    combinations = 0; // reset counter
                    // combine any common rectangles horizontally
                    for (int k=0; k<rectangles.size(); k++) {
                        TaggedRectangle current = rectangles.get(k);
                        for (int h=0; h<rectangles.size(); h++) {
                            TaggedRectangle other = rectangles.get(h);

                            // if both rectangles are the same height and y position
                            if (current.getY() == other.getY() && 
                                    current.getHeight() == other.getHeight()) {

                                // if the current left bound touches the other right bound
                                if (current.getX() == other.getX() + other.getWidth()) {
                                    current.setWidth((int)(current.getWidth() + other.getWidth()));
                                    current.setX((int)other.getX());
                                    rectangles.remove(h); // remove the other rectangle
                                    combinations++;

                                // if the current right bound touches the other left bound
                                } else if (other.getX() == current.getX() + current.getWidth()) {
                                    current.setWidth((int)(current.getWidth() + other.getWidth()));
                                    rectangles.remove(h); // remove the current rectangle
                                    combinations++;
                                }
                            }
                        }
                    }

                    // combine any rectangles vertically
                    for (int k=0; k<rectangles.size(); k++) {
                        TaggedRectangle current = rectangles.get(k);
                        for (int h=0; h<rectangles.size(); h++) {
                            TaggedRectangle other = rectangles.get(h);

                            // don't combine if they have the same tag
                            if (!other.hasSameTag(current)) {
                                continue;
                            }

                            // if both rectangles are the same width and x position
                            if (current.getX() == other.getX() && 
                                    current.getWidth() == other.getWidth()) {

                                // if the current lower bound touches the other upper bound
                                if (current.getY() == other.getY() + other.getHeight()) {
                                    current.setHeight((int)(current.getHeight() + other.getHeight()));
                                    current.setY((int)other.getY());
                                    rectangles.remove(h); // remove the other rectangle
                                    combinations++;

                                // if the current right bound touches the other left bound
                                } else if (other.getY() == current.getY() + current.getHeight()) {
                                    current.setHeight((int)(current.getHeight() + other.getHeight()));
                                    rectangles.remove(h); // remove the current rectangle
                                    combinations++;
                                }
                            }
                        }
                    }

                } while (combinations > 0);
                
                System.out.print(" Combined " + rectanglesBefore + " rectangles into " + rectangles.size() + "...");
                System.out.print(" Generating text file...");
                
                String name = currentFile.getName();
                String fileName = (name.substring(0, name.indexOf(".")));
                // generate the text file after combining all rectangles
                File outFile = new File(outputFolder.getAbsolutePath()+ "/" + fileName + ".txt");
                PrintWriter writer = new PrintWriter(outFile);
                
                if (outputFormat == OutputFormat.KEYPAIRS) {
                    for (TaggedRectangle tr : rectangles) {
                        writer.println(tr.getTag() + seperator +
                                (scale * tr.getX()) + seperator +
                                (scale * tr.getY()) + seperator +
                                (scale * tr.getWidth()) + seperator +
                                (scale * tr.getHeight()) + seperator);
                    }
                } else {
                    writer.println("var data = ["); // start array
                        for (TaggedRectangle tr : rectangles) {
                            writer.print("{\"tag\": \"" + tr.getTag() + "\", ");
                            writer.print("\"x\" : " + (scale * tr.getX()) + ", ");
                            writer.print("\"y\" : " + (scale * tr.getY()) + ", ");
                            writer.print("\"width\" : " + (scale * tr.getWidth()) + ", ");
                            writer.print("\"height\" : " + (scale * tr.getHeight()) + "},");
                            writer.println();
                    }
                    
                    writer.println("];"); // finish array
                }
                
                System.out.println(" Done");
                
                writer.close();
            } catch(Exception e) {
                System.err.println("Error processing the image '" + currentFile.getName() + "': " + e);
                e.printStackTrace();
            }
        }
        
        System.out.println("Finished processing " + imagesFound + " image(s).");
        
    }
    
}
