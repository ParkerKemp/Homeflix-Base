package com.thundercats.homeflix_base;

import com.xuggle.xuggler.Converter;
import java.io.File;
import java.io.IOException;
 
class Simpleh264Transcoder {
     public static void transcode(File inputFile, File presetsFile) {
          //This is the converter object we will use.
          Converter converter = new Converter();
 
          //These are the arguments to pass to the converter object.
          //For H264 transcoding, the -vpreset option is very
          //important. Here, presetsFile is a File object corresponding
          //to a libx264 video presets file. These are in the
          // /usr/local/share/ffmpeg directory.
          String[] arguments = {
          inputFile.getAbsolutePath(),
          "-acodec", "aac",//"libfaac",
          "-asamplerate", "44100",
          "-vcodec", "libx264",
          "-vpreset", presetsFile.getAbsolutePath(),
          inputFile.getParent() + "/" + inputFile.getName() + ".mp4"
          };
 
          try {
               //Finally, we run the transcoder with the options we provided.
               converter.run(
                    converter.parseOptions(
                         converter.defineOptions(), arguments)
               );
          } catch (Exception e) {
               e.printStackTrace();
          }
 
     }
}