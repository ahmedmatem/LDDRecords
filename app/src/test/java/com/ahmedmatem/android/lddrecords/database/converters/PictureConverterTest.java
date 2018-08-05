package com.ahmedmatem.android.lddrecords.database.converters;

import com.ahmedmatem.android.lddrecords.models.Picture;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PictureConverterTest {
    public static final String PATH_DELIMITER = "#";
    public static final String stringPhotos = "asdad" + PATH_DELIMITER;

    @Test
    public void splitStringPhotosIntoList() {
        List<Picture> pictures = PictureConverter.toPhotoList(stringPhotos);
        List<Picture> nullPictures = PictureConverter.toPhotoList("");
        assertNull(nullPictures);
        assertEquals(0, new ArrayList<Picture>().size());
    }

    @Test
    public void photosAsString() {
    }
}