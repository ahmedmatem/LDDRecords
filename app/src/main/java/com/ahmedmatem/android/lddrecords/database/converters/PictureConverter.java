package com.ahmedmatem.android.lddrecords.database.converters;

import android.arch.persistence.room.TypeConverter;

import com.ahmedmatem.android.lddrecords.models.Picture;

import java.util.ArrayList;
import java.util.List;

public class PictureConverter {
    public static final String PATH_DELIMITER = "#";

    @TypeConverter
    public static List<Picture> toPhotoList(String photos){
        if(photos == null || photos.isEmpty()){
            return null;
        }
        List<Picture> photoList = new ArrayList<Picture>();
        String[] paths = photos.split(PATH_DELIMITER);
        for (String path : paths) {
            photoList.add(new Picture(path));
        }
        return photoList;
    }

    @TypeConverter
    public static String photosAsString(List<Picture> photos) {
        if (photos == null) {
            return null;
        }

        if(photos.size() > 0) {
            StringBuilder result = new StringBuilder();
            for (Picture photo : photos) {
                result.append(photo.getPath()).append(PATH_DELIMITER);
            }
            return result.deleteCharAt(result.lastIndexOf(PATH_DELIMITER)).toString();
        } else {
            return null;
        }
    }
}
