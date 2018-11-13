import com.opencsv.CSVReader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeoJsonGenerator {

    public static void main(String[] args) throws IOException {

        Reader reader = Files.newBufferedReader(Paths.get("/Users/mac/Downloads/modified_ura_2014_planning_area_mod.csv"));
        CSVReader csvReader = new CSVReader(reader);
        String[] nextRecord;
        String geoJsonString="var campus={";
        String geoFeatureStringStart="{\n" +
                "\"type\": \"FeatureCollection\",\n" +
                "\"crs\": { \"type\": \"name\", \"properties\": { \"name\": \"urn:ogc:def:crs:OGC:1.3:CRS84\" } },\n" +
                "\"features\": [";
        String geoFeatureStringEnd="]};";
        int i=0;
        String total="";
        while ((nextRecord = csvReader.readNext()) != null) {

            String cordinates=nextRecord[2];
            //System.out.println(cordinates.split(":").length);
            //System.out.println(cordinates);
            String s="{ \"type\": \"Feature\", \"properties\": {\"id\":\""+nextRecord[0]+"\" ,\"hired_state\":\"0\", \"name\": \""+nextRecord[1]+"\"}, \"geometry\": { \"type\": \"MultiPolygon\", \"coordinates\":[[[";
            for(int itr=0;itr<cordinates.split(":").length;itr++){
//                System.out.println(cordinates.split(":")[itr]);
                s=s+"["+cordinates.split(":")[itr].replace(" ",",")+"]";
                if(itr==(cordinates.split(":").length-1)){
                    s=s+"";
                }
                else{
                    s=s+",";
                }
            }

            s=s+"]]]}},";


            total=total+s;

            i++;


        }


        geoJsonString=geoJsonString+geoFeatureStringStart+total+geoFeatureStringEnd;
        System.out.print(geoJsonString);

        FileWriter writer = new FileWriter("/Users/mac/Downloads/ura-planning-areas.js");
        writer.write(geoJsonString);
        writer.close();


    }
}
