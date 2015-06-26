package org.magnum.dataup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.logging.annotations.Param;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.model.VideoStatus.VideoState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javassist.bytecode.stackmap.TypeData.ClassName;
import retrofit.mime.TypedFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class VideoController {

	private Map<Long, Video> videos = new HashMap<Long, Video>();

	private static final AtomicLong currentId = new AtomicLong(0L);
	
    private void checkAndSetId(Video V) {
        if(V.getId() == 0){
            V.setId(currentId.incrementAndGet());
        }
    }

//    @RequestMapping(value="/video/{id}/data", method=RequestMethod.GET)
//    public @ResponseBody 
    
    @RequestMapping(value="/video/{id}/data", method=RequestMethod.POST)
    public @ResponseBody VideoStatus addVideoData (
    		@PathVariable("id") Long videoId,
    		@RequestParam("data") MultipartFile videoData,
    		HttpServletResponse response
    ) throws IOException {
    	
    	Logger log = Logger.getLogger(ClassName.class.getName());
    	
    	VideoStatus status = new VideoStatus(VideoState.READY);

    	log.log(Level.INFO, "get request with ID " + videoId, 1);
    	
    	Video v = videos.get(videoId);
    	
    	if (null == v) {
    		status.setState(VideoState.PROCESSING);
    		log.log(Level.INFO, "Sending error", 1);
    		response.sendError(404);
    		log.log(Level.INFO, "WOW", 1);
    	}
    	
    	return status;
    }
    
    @RequestMapping(value="/video", method=RequestMethod.GET)
    public @ResponseBody Collection<Video> getVideosList () {
    	
    	Collection<Video> c = videos.values();
    	
		return c;
    	
    }
    
	@RequestMapping(value="/video", method=RequestMethod.POST)
	public @ResponseBody Video postVideo (@RequestBody Video v) {
		
		Video v_in = new Video();
		v_in.setDuration(v.getDuration());
		v_in.setTitle(v.getTitle());
		v_in.setContentType(v.getContentType());
		v_in.setLocation(v.getLocation());
		v_in.setSubject(v.getSubject());
		checkAndSetId(v_in);
		v_in.setDataUrl("AAAA");
		
		videos.put(v_in.getId(), v_in);
		
		return v_in;
	}
	
}
