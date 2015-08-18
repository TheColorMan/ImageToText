# ImageToText
Easy level building and file generation for games

Usually, building levels for games is a huge pain. Unfortunately, it's slow and annoying to parse an easy to 
read and visualize image file into the blocks used in your platformer or dungeon crawl game. 
Even then, it's usually makes the game run much slower because your level is made up of hundreds of small blocks.

The ImageToText utility will take .png, .jpg, or .gif images that are easy for you to read and turn them into text files that are easy for your game to read. You can assign different colors to different tags so your game can identify what different blocks are supposed to be in a simple config file.

## Running
Since it's a .jar, you need java to run it.

You have to specify the input directory and the output directory (they can be the same place). If no directories are given, it defaults to where the jar is located. Every image found in the input directory will be converted to a text file of the same name as the image and placed into the output directory.

To give the output and input directory, type `-inf <path>' for input or '-outf <path>' for output. This will make the input folder be "img" and the output be "data".

```
java -jar ImageToText.jar -inf img -outf data
```

The program will say how many images it found in the input directory and give info about how it processed them. It will combine rectangles with the same tags in your image together so there are less blocks used in your level but still keep the same overall shape.

```
C:\Users\Me\Documents\ImageToTextUtil>java -jar ImageToText.jar -inf test/img -outf test/data
Input directory: C:\Users\Me\Documents\ImageToTextUtil\test\img
Output directory: C:\Users\Me\Documents\ImageToTextUtil\test\data
Reading level1.png... Combined 404 rectangles into 16... Generating text file... Done
Reading level2.png... Combined 296 rectangles into 68... Generating text file... Done
Reading level3.png... Combined 371 rectangles into 13... Generating text file... Done
Finished processing 3 image(s).
```

## Config file
The config file is used to tell the program what tags to assign different colors and to give information about what format you want the output file to be in. Every line starts with an option, followed by any parameters for that option seperated by spaces. The order the options are in doesn't matter. The config file must be in the same directory as the program.

####Parameters
  * ```DEFINE <string tag> <int red> <int green> <int blue>``` - Define a color to a tag. When the program scans the input image, it will assign any pixel with the given color to the given tag. Any color that isn't defined is ignored.
  * ```SCALE <float scale>``` - A number that the size and position of the blocks is multiplied by in the output file. Useful if the scale in your image is different than the scale in your game.
  * ```SEPERATOR <string sep>``` - If your output format is ```KEYPAIRS```, this string will seperate the tags and info about each rectangle on every line. If no seperator is specified, a space ' ' will be the seperator
  * ```OUTPUT_FORMAT <JSON|KEYPAIRS>``` - Specify the format you want the output file to be in.

Example config file:

```
SCALE 10
OUTPUT_FORMAT JSON
DEFINE BLOCK 0 0 0
DEFINE LAVA 255 0 0
```

This config file will give black pixels the ```BLOCK``` tag and red pixels the ```LAVA``` tag.

####Output file
The output text file(s) generated will go into the specified output folder and will have the same name as the image used to generate the file.

Example ```JSON``` output file
```
var data = [
{"tag": "BLOCK", "x" : 290.0, "y" : 240.0, "width" : 110.0, "height" : 60.0},
{"tag": "BLOCK", "x" : 150.0, "y" : 310.0, "width" : 110.0, "height" : 40.0},
{"tag": "BLOCK", "x" : 50.0, "y" : 350.0, "width" : 370.0, "height" : 90.0},
];
```

Example ```KEYPAIRS``` output file (using the same image)
```
BLOCK 290.0 240.0 110.0 60.0 
BLOCK 150.0 310.0 110.0 40.0 
BLOCK 50.0 350.0 370.0 90.0 
```

Each line in the ```KEYPAIRS``` output file starts with the tag, followed by the rectangle's x position, y position, width, and height in that order.
