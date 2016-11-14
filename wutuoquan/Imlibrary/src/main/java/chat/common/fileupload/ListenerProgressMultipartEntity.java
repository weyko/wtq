package chat.common.fileupload;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import chat.common.util.output.ShowUtil;
import chat.image.ProgressImageView;

/**
 * 
 * 监听文件流输出进度
 * ByProgressMultipartEntity
 *
 */
public class ListenerProgressMultipartEntity extends MultipartEntity {
	private final ProgressImageView.ImageUploadListener uploadListener;
	private int fileLen = 0;

	public ListenerProgressMultipartEntity(ProgressImageView.ImageUploadListener uploadListener) {
		super();
		this.uploadListener = uploadListener;
	}

	public ListenerProgressMultipartEntity(HttpMultipartMode mode,
										   final ProgressImageView.ImageUploadListener uploadListener, long fileLen) {
		super(mode);
		this.uploadListener = uploadListener;
		this.fileLen = (int) fileLen;
	}

	public ListenerProgressMultipartEntity(HttpMultipartMode mode,
										   final String boundary, final Charset charset,
										   final ProgressImageView.ImageUploadListener uploadListener) {
		super(mode, boundary, charset);
		this.uploadListener = uploadListener;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new UploadFilterOutputStream(outstream,
				this.uploadListener));
	}

	public class UploadFilterOutputStream extends FilterOutputStream {
		private final ProgressImageView.ImageUploadListener uploadListener;
		private int progress = 0;

		public UploadFilterOutputStream(OutputStream out,
				final ProgressImageView.ImageUploadListener uploadListener) {
			super(out);
			this.uploadListener = uploadListener;
		}

		public void write(byte[] buffer, int offset, int length)
				throws IOException {
			out.write(buffer, offset, length);
			progress += length;
			int pro = 0;
			if (uploadListener != null && fileLen > 0) {
				pro = progress * 100 / fileLen;
				if (pro > 100) {
					pro = 100;
				}
				uploadListener.onProgressUpdate(pro);
			}
			ShowUtil.log("weyko",
					"------------ListenerProgressMultipartEntity1----uploadListener==null("
							+ (uploadListener == null) + ")----progress=" + pro
							+ "-read=" + progress);
		}

		public void write(int oneByte) throws IOException {
			// super.write(oneByte);
			out.write(oneByte);
			progress++;
			ShowUtil.log("weyko",
					"------------ListenerProgressMultipartEntity2--------progress="
							+ progress + "-oneByte=" + oneByte);
			if (uploadListener != null && fileLen > 0) {
				uploadListener.onProgressUpdate(progress * 100 / fileLen);
			}
		}
	}
}
