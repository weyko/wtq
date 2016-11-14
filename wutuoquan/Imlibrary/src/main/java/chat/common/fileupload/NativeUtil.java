/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chat.common.fileupload;

import android.graphics.Bitmap;

public class NativeUtil {
	private static int DEFAULT_QUALITY = 95;

	/*public static void compressBitmap(Bitmap bit, String fileName,
			boolean optimize) {
		compressBitmap(bit, bit.getWidth(), bit.getHeight(), DEFAULT_QUALITY, fileName.getBytes(), optimize);
	}*/
	
	public static void saveBitmap(Bitmap bit, int quality, String fileName,
			boolean optimize) {
		compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality,
				fileName.getBytes(), optimize);
	}

	public static native String compressBitmap(Bitmap bit, int w, int h,
			int quality, byte[] fileNameBytes, boolean optimize);
	static {
		System.loadLibrary("bitherjni");
		System.loadLibrary("jpegbither");
	}
}
