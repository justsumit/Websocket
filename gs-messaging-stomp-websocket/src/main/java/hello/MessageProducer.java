package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageProducer {
	
	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping("/hello")
    public  void produceMessage() throws Exception {
		FileTail fileTail = new FileTail(System.getProperty("user.dir")+"/"+"a.txt",template,"/topic/logs");
		Thread watcherThread = new Thread(fileTail);
		watcherThread.start();

    }

}
