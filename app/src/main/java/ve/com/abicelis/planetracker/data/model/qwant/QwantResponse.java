package ve.com.abicelis.planetracker.data.model.qwant;

import java.util.List;

import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesFlights;

/**
 * Created by abicelis on 21/7/2017.
 */

public class QwantResponse {

    private QwantData data;
    public QwantData getData() {return data;}



    public class QwantData {
        private QwantResult result;
        public QwantResult getResult() {return result;}
    }

    public class QwantResult {
        private List<QwantItems> items;
        public List<QwantItems> getItems() {return items;}
    }

    public class QwantItems {

        private String media;
        private String thumbnail;
        private int thumb_width;
        private int thumb_height;
        private int width;
        private int height;
        private String url;

        public String getMedia() {return media;}
        public String getThumbnail() {return thumbnail;}
        public int getThumb_width() {return thumb_width;}
        public int getThumb_height() {return thumb_height;}
        public int getWidth() {return width;}
        public int getHeight() {return height;}
        public String getUrl() {return url;}
    }
}
