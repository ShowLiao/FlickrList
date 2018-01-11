package com.example.show.testflicker;

public class FlickrMgr {

    private static final String FLICKR_API_KEY = "&api_key=6bf318919bbbc455f3573d18798a58e3";
    private static final String RIDECELL_FLICKR_SECRET = "177e9b4d7f3766dc";

    private static final String FLICKR_SERVICE_URL = "https://www.flickr.com/services/rest/?method=";
    private static final String FLICKR_METHOD_SEARCH = "flickr.photos.search";
    private static final String FLICKR_METHOD_GETSIZES = "flickr.photos.getSizes";
    private static final String FLICKR_METHOD_GETINFO = "flickr.photos.getInfo";

    private static final String FLICKR_STRING_TAGS = "&tags=";
    private static final String FLICKR_STRING_FORMAT = "&format=json";
    private static final String FLICKR_STRING_PHOTO_ID = "&photo_id=";


    public static final int FLICKR_SEARCH = 1;
    private static final int FLICKR_GET_SIZES = 2;
    public static final int FLICKR_GET_INFO = 3;

    public static final int FLICKR_IMG_SQUARE = 1;
    public static final int FLICKR_IMG_MED = 2;



    private static String createURL(int methodID, String para) {
        String url = FLICKR_SERVICE_URL;

        switch (methodID) {

            case FLICKR_SEARCH:
                url = url + FLICKR_METHOD_SEARCH + FLICKR_API_KEY + FLICKR_STRING_TAGS + para + FLICKR_STRING_FORMAT;
                break;
            case FLICKR_GET_SIZES:
                url = url + FLICKR_METHOD_GETSIZES + FLICKR_API_KEY + FLICKR_STRING_PHOTO_ID + para + FLICKR_STRING_FORMAT;
                break;
            case FLICKR_GET_INFO:
                url = url + FLICKR_METHOD_GETINFO + FLICKR_API_KEY + FLICKR_STRING_PHOTO_ID + para + FLICKR_STRING_FORMAT;;
                break;
        }

        return url;
    }

    public static String searchPhoto(String searchStr) {

        String url = createURL(FLICKR_SEARCH, searchStr);
        return url;

    }

    public static String getPhotoSizes(String searchStr) {

        String url = createURL(FLICKR_GET_SIZES, searchStr);
        return url;

    }

    public static String getPhotoInfo(String id) {

        String url = createURL(FLICKR_GET_INFO, id);
        return url;

    }

    public static String createPhotoURL(int photoType, ImgConetent imgCon) {
        String tmp = null;
        tmp = "http://farm" + imgCon.farm + ".staticflickr.com/" + imgCon.server + "/" + imgCon.id + "_" + imgCon.secret;// +".jpg";
        switch (photoType) {
            case FLICKR_IMG_SQUARE:
                tmp += "_t";
                break;
            case FLICKR_IMG_MED:
                tmp += "_z";
                break;

        }
        tmp += ".jpg";
        return tmp;
    }

}


