package ch.appquest.indiana_phones.appquest4_schatzkarte;

import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raphael.haenni on 17.11.2017.
 */

public class Position {
    private List<Point> coordinates = new ArrayList();
    private static String filename = "coordinates.txt";
    private static String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/appquest/";

    public Position( Point[] pArr ) {
        for(Point point : pArr) {
            this.coordinates.add(point);
        }
        this.overwrite();
    }

    public Position()
    {
        File myDir = new File( this.path );
        myDir.mkdirs();
        String fname = filename;
        try {
            FileReader fReader = new FileReader(myDir + "/" + fname);
            BufferedReader reader = new BufferedReader(fReader);
            String cordString = reader.readLine();
            Log.d("GIB US", "String: " + cordString);
            if (cordString != null)
            {
                reader.close();
                fReader.close();
                String[] pointString = cordString.split("\\;");
                for (String ps: pointString)
                {
                    String[] spString = ps.split("\\,");
                    coordinates.add(new Point(Integer.parseInt(spString[0]), Integer.parseInt(spString[1])));
                }
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void overwrite() {
        Log.e("LISTOFARRAY", this.coordinates.toString() );

        //Create new directory, if it doesn't exist
        File myDir = new File( this.path );
        myDir.mkdirs();

        try {
            FileWriter file = new FileWriter(myDir + "/" + this.filename);
            BufferedWriter out = new BufferedWriter(file);
            String cordString = "";
            for (Point p: coordinates)
            {
                cordString += p.x + "," + p.y + ";";
            }
            if (cordString != "")
            {
                cordString = cordString.substring(0, cordString.length() - 1);
            }
            out.write(cordString);
            out.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Point> getPoints(){
        return this.coordinates;
    }
}