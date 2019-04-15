package hello;

import java.io.File;
import java.io.RandomAccessFile;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class FileTail implements Runnable {

	private String fileName;
	private int intervalTime = 1000;
	private volatile boolean stopThread = false;
	private File fileToWatch;
	private long lastKnownPosition = 0;
	private SimpMessagingTemplate template;
	private String topic;

	public FileTail(String fileName,SimpMessagingTemplate template,String topic) {
		this(fileName, 1000,template,topic);
	}

	public FileTail(String fileName, int intervalTime,SimpMessagingTemplate template,String topic) {
		this.fileName = fileName;
		this.intervalTime = intervalTime;
		this.fileToWatch = new File(fileName);
		this.template=template;
		this.topic=topic;
	}

	@Override
	public void run() {

		if (!fileToWatch.exists()) {
			throw new IllegalArgumentException(fileName + " not exists");
		}
		try {
			while (!stopThread) {
				Thread.sleep(intervalTime);
				long fileLength = fileToWatch.length();

				if (fileLength < lastKnownPosition) {
					lastKnownPosition = 0;
				}
				if (fileLength > lastKnownPosition) {
					RandomAccessFile randomAccessFile = new RandomAccessFile(fileToWatch, "r");
					randomAccessFile.seek(lastKnownPosition);
					String line = null;
					while ((line = randomAccessFile.readLine()) != null) {
						//System.out.println(line);
						template.convertAndSend(this.topic, line);
					}
					lastKnownPosition = randomAccessFile.getFilePointer();
					randomAccessFile.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			stopRunning();
		}

	}

	public boolean isStopThread() {
		return stopThread;
	}

	public void setStopThread(boolean stopThread) {
		this.stopThread = stopThread;
	}

	public void stopRunning() {
		stopThread = false;
	}

}