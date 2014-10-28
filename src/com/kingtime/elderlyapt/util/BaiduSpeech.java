package com.kingtime.elderlyapt.util;

import android.content.Context;
import android.media.AudioManager;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;

/**
 * 百度语音合成
 * 
 * @author xp
 * @created 2014年8月16日
 */
public class BaiduSpeech {
	
	public static SpeechSynthesizer getSpeechSynthesizer(Context context,SpeechSynthesizerListener listener){
		SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(context,"holder", listener);

        speechSynthesizer.setApiKey("kslfNHwxFVQG1GWrIOriNWvq", "P8sh8YnPqG0DSqWCpU0ck9sSqgBedzj1");
        speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, "1");
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, "4");
        return speechSynthesizer;
	}

}
