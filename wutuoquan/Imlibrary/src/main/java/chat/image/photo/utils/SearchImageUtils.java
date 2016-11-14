package chat.image.photo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import chat.image.photo.adapter.PhotoWallAdapter;
import chat.image.photo.model.PhotoAlbumLVItem;
import chat.image.photo.model.PhotoSelectedItem;
import chat.image.photo.model.PhotoWallItem;

public class SearchImageUtils {
    /**
     * this tag used for recognition recently photo or folder photo
     */
    public static final String RECENTLY = "recently";

    /**
     * get total images in this folder
     *
     * @param folder
     * @return
     */
    public static int getImageCount(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file != null && file.exists() && Utility.isImage(file.getName())) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 获取目录中最新的一张图片的绝对路径。
     */
    public static String getFirstImagePath(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                if (file != null && Utility.isImage(file.getName())) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    public static ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider(Context context) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;
        ContentResolver mContentResolver = context.getContentResolver();
        // 只查询jpg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<PhotoAlbumLVItem> list = null;
        if (cursor != null) {
            if (cursor.moveToLast()) {
                //路径缓存，防止多次扫描同一目录
                HashSet<String> cachePath = new HashSet<String>();
                list = new ArrayList<PhotoAlbumLVItem>();
                while (true) {
                    // 获取图片的路径
                    String imagePath = cursor.getString(0);
                    if (imagePath != null) {
                        File imageFile = new File(imagePath);
                        if (/*imageFile != null && */imageFile.exists()) {
                            File parentFile = imageFile.getParentFile();
                            String parentPath = parentFile.getAbsolutePath();
                            //不扫描重复路径
                            if (!cachePath.contains(parentPath)) {
                                list.add(new PhotoAlbumLVItem(parentPath, getImageCount(parentFile),
                                        getFirstImagePath(parentFile)));
                                cachePath.add(parentPath);
                            }
                        }
                    }
                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }
        return list;
    }

    public static String getDefaultImagePath(Context context) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;
        ContentResolver mContentResolver = context.getContentResolver();
        // 只查询jpg和png的图片
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        String parentPath = "";
        if (cursor != null) {
            if (cursor.moveToLast()) {
                String imagePath = cursor.getString(0);
                if (imagePath != null) {
                    File imageFile = new File(imagePath);
                    if (/*imageFile != null && */imageFile.exists()) {
                        File parentFile = imageFile.getParentFile();
                        parentPath = parentFile.getAbsolutePath();
                    }
                }
            }
            cursor.close();
        }
        return parentPath;
    }

    /**
     * 获取指定路径下的所有图片文件。
     */
    public static ArrayList<PhotoWallItem> getAllImagePathsByFolder(
            String folderPath, ArrayList<PhotoSelectedItem> selectedPhotos) {
        ArrayList<PhotoWallItem> datas = new ArrayList<PhotoWallItem>();
        if (folderPath == null || folderPath.length() == 0) {
            return datas;
        }
        File folder = new File(folderPath);
        String[] allFileNames = folder.list();
        if (allFileNames == null || allFileNames.length == 0) {
            return datas;
        }
        for (int i = allFileNames.length - 1; i >= 0; i--) {
            if (Utility.isImage(allFileNames[i])) {
                String filePath = folderPath + File.separator + allFileNames[i];
                File file = new File(filePath);
                if (file.exists() && file.length() > 0) {
                    PhotoWallItem item = new PhotoWallItem();
                    item.photoFilePath = filePath;
                    checkIsSelected(item, folderPath, selectedPhotos);
                    datas.add(item);
                }
            }
        }
        sortByModifyTime(datas);
        return datas;
    }

    private static void sortByModifyTime(ArrayList<PhotoWallItem> selectedPhotos) {
        //按修改时间从大到小排序
        Collections.sort(selectedPhotos, new Comparator<PhotoWallItem>() {
            public int compare(PhotoWallItem a, PhotoWallItem b) {
                File fileA = new File(a.photoFilePath);
                File fileB = new File(b.photoFilePath);
                if (fileB.lastModified() > fileA.lastModified()) {
                    return 1;
                } else if (fileB.lastModified() == fileA.lastModified()) {
                    return 0;
                } else {
                    return -1;
                }
                //return -1;
            }
        });
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    public static ArrayList<PhotoWallItem> getLatestImagePaths(Context context,
                                                               int maxNum, ArrayList<PhotoSelectedItem> selectedPhotos) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = context.getContentResolver();
        Cursor cursor = mContentResolver.query(mImageUri,
                new String[]{key_DATA}, key_MIME_TYPE + "=? or "
                        + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<PhotoWallItem> datas = new ArrayList<PhotoWallItem>();
        PhotoWallItem item = new PhotoWallItem();
        item.actionType = PhotoWallAdapter.ACTION_TYPE_CAMARA;
        datas.add(item);

        if (cursor != null) {
            if (cursor.moveToLast()) {
                while (true) {
                    String path = cursor.getString(0);
                    if (TextUtils.isEmpty(path))//fix by weyko 2016.02.17 NullPointerException
                        continue;
                    File file = new File(path);
                    if (file.exists() && file.length() > 0) {
                        PhotoWallItem itemPhoto = new PhotoWallItem();
                        itemPhoto.photoFilePath = path;
                        checkIsSelected(itemPhoto, RECENTLY, selectedPhotos);
                        datas.add(itemPhoto);
                    }
                    if (datas.size() >= maxNum
                            || !cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }
        return datas;
    }

    private static void checkIsSelected(PhotoWallItem item, String dir, ArrayList<PhotoSelectedItem> selectedPhotos) {
        if (selectedPhotos == null) {
            return;
        }
        for (int i = 0; i < selectedPhotos.size(); i++) {
            PhotoSelectedItem searchItem = selectedPhotos.get(i);
            if (searchItem.filePath.compareToIgnoreCase(item.photoFilePath) == 0 &&
                    searchItem.dir.compareToIgnoreCase(dir) == 0) {
                item.isSelected = true;
                break;
            }
        }
    }
}