package org.magnum.dataup;

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
import retrofit.http.Part;
import retrofit.mime.TypedFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class VideoController {

	private Map<Long, Video> videos = new HashMap<Long, Video>();

	private static final AtomicLong currentId = new AtomicLong(0L);
	
	private VideoFileManager videoFileManager;
	
    private void checkAndSetId(Video V) {
        if(V.getId() == 0){
            V.setId(currentId.incrementAndGet());
        }
    }

    @RequestMapping(value="/video/{id}/rating", method=RequestMethod.GET)
    public @ResponseBody Integer getRating (@PathVariable("id") Long videoId) {
        Video video = videos.get(videoId);

        return video.getRating();
    }

    @RequestMapping(value="/video/{id}/rating/{stars}", method=RequestMethod.POST)
    public void setRating (
        @PathVariable("id") Long videoId,
        @PathVariable("stars") Integer rating
    ) {
        Video video = videos.get(videoId);

        if (null != video) {
            video.setRating(rating);
        }

        return;
    }

    @RequestMapping(value="/video/{id}/data", method=RequestMethod.GET)
    public void getVideoData (
    		@PathVariable("id") Long videoId,
    		HttpServletResponse response
    ) throws IOException {
    	
    	Video video = videos.get(videoId);
    	
    	if (video == null) {
    		response.sendError(404);
    		return;
    	}
    	
    	videoFileManager = VideoFileManager.get();
    	
    	response.setContentType("video/mp4");
    	videoFileManager.copyVideoData(video, response.getOutputStream());
    }
    
    @RequestMapping(value="/video/{id}/data", method=RequestMethod.POST)
    public @ResponseBody VideoStatus addVideoData (
    		@PathVariable("id") Long videoId,
    		@RequestParam("data") List<MultipartFile> files,
    		HttpServletResponse response
    ) throws IOException {
    	
    	VideoStatus status = new VideoStatus(VideoState.READY);

    	Video v = videos.get(videoId);
    	
    	if (null == v) {
    		// There is no state 'ERROR'. Processing is set to mark the state is not READY
    		status.setState(VideoState.PROCESSING);
    		response.sendError(404);
    		return status;
    	}
    	
    	videoFileManager = VideoFileManager.get();
    	
    	videoFileManager.saveVideoData(v, files.get(0).getInputStream());
    	
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
		v_in.setDataUrl("FILE_PATH");
		
		videos.put(v_in.getId(), v_in);
		
		return v_in;
	}
	
}
