package com.takashi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    private static PDollarRecognizer recognizer = new PDollarRecognizer();

    public static void main(String[] args) throws FileNotFoundException {

        for (int i = 0; i < args.length; i++) {
//            System.out.println("Argument #" + i + " = " + args[i]);
            if(args[i].equals("-t")){
                addGesture(args[i+1]);
                i++;
            } else if(args[i].equals("-r")){
                recognizer.clearCache();
            } else if(!args[i].equals(null)){
                runEvent(args[i]);
            }
        }
        if(args.length == 0){
            System.out.println("PDollar Help " +
                    "\n\tpdollar -t <gesturefile>" +
                        "\n\t\tAdds the gesture file to the list of gesture templates" +
                    "\n\n\tpdollar ‐r " +
                        "\n\t\tClears the templates "+
                    "\n\n\tpdollar <eventstream> " +
                        "\n\t\tPrints the name of gestures as they are recognized from the event stream."
            );
        }
     }

    private static ArrayList<Point> parseGesturePoints(Scanner scan){
        ArrayList<Point> list = new ArrayList<Point>();
        int IDCount = 0;
        while (scan.hasNextLine()){
            String temp = scan.nextLine();
            if (temp.equals("BEGIN")){
                IDCount++;
                continue;
            }
            else if (temp.equals("END")){
                continue;
            }

            String[] tempSplit = temp.split(",");
            Point newPoint = new Point(Double.parseDouble(tempSplit[0]), Double.parseDouble(tempSplit[1]), IDCount);
            list.add(newPoint);
        }
        scan.close();
        return list;
    }

    private static void addGesture(String path) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(path));
        String gestureTitle = "";
        if(scan.hasNextLine()){
            gestureTitle = scan.nextLine();
        }
        ArrayList<Point> list = parseGesturePoints(scan);
        recognizer.addGesture(gestureTitle, list);
        System.out.println("Gesture Added: " + gestureTitle);
    }

    private static void runEvent(String path) throws FileNotFoundException {
        Scanner scanEvent = new Scanner(new File(path));

        ArrayList<Point> eventList = new ArrayList<Point>();
        int eventIDCount = 0;
        while (scanEvent.hasNextLine()){
            String temp = scanEvent.nextLine();
            if (temp.equals("MOUSEDOWN")){
                eventIDCount++;
                continue;
            }
            else if (temp.equals("MOUSEUP")){
                continue;
            }
            else if (temp.equals("RECOGNIZE")){
                RecognizerResults results = recognizer.Recognize(eventList);
                System.out.println("Results name: " + results.mName);
                System.out.println("Results score: " + results.mScore);
                eventList = new ArrayList<Point>();
                eventIDCount = 0;
                continue;
            }

            String[] tempSplit = temp.split(",");
            Point newPoint = new Point(Double.parseDouble(tempSplit[0]), Double.parseDouble(tempSplit[1]), eventIDCount);
            eventList.add(newPoint);
        }
        scanEvent.close();
    }

}
