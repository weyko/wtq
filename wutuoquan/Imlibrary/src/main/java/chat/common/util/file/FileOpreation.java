package chat.common.util.file;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

/**
 * 文件操作工具类
 *
 */
public class FileOpreation {

	public static boolean newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdirs();
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public static boolean delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			File myDelFile = new File(filePath);
			if (/*myDelFile != null && */myDelFile.exists()) {
				if (myDelFile.delete()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	public static boolean delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			String filePath = folderPath;
			File myFilePath = new File(filePath);
			myFilePath.delete();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
			}
		}
	}

	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}

		FileInputStream stream = null ;
		ByteArrayOutputStream out = null;
		try {
			stream = new FileInputStream(f);
			out = new ByteArrayOutputStream(1024);

			byte[] b = new byte[1024];
			int n;
			while ((n = stream.read(b)) != -1) {
				out.write(b, 0, n);
			}
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					stream = null ;
				}

				if(out != null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					out = null ;
				}
		}

		return null;
	}

	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		FileOutputStream fstream = null;
		try {
			file = new File(outputFile);
			fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if(fstream != null)
			{
				try {
					fstream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		InputStream inStream = null;
		FileOutputStream fos = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldPath); // 读入原文件
				fos = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fos.write(buffer, 0, byteread);
				}
				fos.close();
				inStream.close();
				return true;
			}
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
			return false;

		} finally {
			try {
				if (fos != null)
					fos.close();
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();

		}
	}

	public static void writeObjectToFile(Object obj, String filePath) {
		if(filePath==null)
			return;
		File file = new File(filePath);
		FileOutputStream out=null;
		try {
			File dir = file.getParentFile();
			if(dir==null)
			{
				return;
			}
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
			objOut.flush();
			objOut.close();
			System.out.println("write object success!");
		} catch (IOException e) {
			System.out.println("write object failed");
			e.printStackTrace();
		} finally {
			if(out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void writeObjectToFile(Object obj, String dirPath, String fileName) {
		File dirFile = new File(dirPath);
		FileOutputStream out = null;
		try {
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			File file = new File(dirFile, fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			ObjectOutputStream objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
			objOut.flush();
			objOut.close();
			System.out.println("write object success!");
		} catch (IOException e) {
			System.out.println("write object failed");
			e.printStackTrace();
		} finally {
			if(out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Object readObjectFromFile(String filePath) {
		Object temp = null;
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		FileInputStream in = null;
		ObjectInputStream objIn = null;
		try {
			in = new FileInputStream(file);
			objIn = new ObjectInputStream(in);
			temp = objIn.readObject();
			in.close();
			objIn.close();
			System.out.println("read object success!");
		} catch (IOException e) {
			System.out.println("read object failed");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objIn != null)
					objIn.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return temp;
	}
}