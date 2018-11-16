package engine.audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static engine.audio.AudioMaster.checkALError;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_close;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by eirik on 26.07.2017.
 */
public class AudioUtils {


    public static ShortBuffer readVorbis(String resource, STBVorbisInfo info) {
        return readVorbis(resource, 32 * 1024, info);
    }

    public static ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) {
        ByteBuffer vorbis;
        try {
            vorbis = AudioIoUtils.ioResourceToByteBuffer(resource, bufferSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IntBuffer error   = BufferUtils.createIntBuffer(1);
        long      decoder = stb_vorbis_open_memory(vorbis, error, null);
        if (decoder == NULL) {
            throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
        }

        stb_vorbis_get_info(decoder, info);

        int channels = info.channels();

        int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

        ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);

        pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
        stb_vorbis_close(decoder);

        return pcm;
    }



    public static int initSoundBuffer(String filename){
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            int bufferPointer = alGenBuffers();
            ShortBuffer pcm = AudioUtils.readVorbis(filename, 32 * 1024, info);

            //Find the correct OpenAL format
            int format = -1;
            if (info.channels() == 1) {
                format = AL_FORMAT_MONO16;
            } else if (info.channels() == 2) {
                format = AL_FORMAT_STEREO16;
            }

            //copy to buffer
            alBufferData(bufferPointer, format, pcm, info.sample_rate());
            checkALError();
            return bufferPointer;
        }
    }

    public static void terminate() {
    }
}
