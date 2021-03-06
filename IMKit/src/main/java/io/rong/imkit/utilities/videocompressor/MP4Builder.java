//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities.videocompressor;

import android.annotation.TargetApi;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox.Entry;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.Matrix;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@TargetApi(16)
public class MP4Builder {
    private MP4Builder.InterleaveChunkMdat mdat = null;
    private Mp4Movie currentMp4Movie = null;
    private FileOutputStream fos = null;
    private FileChannel fc = null;
    private long dataOffset = 0L;
    private long writedSinceLastMdat = 0L;
    private boolean writeNewMdat = true;
    private HashMap<Track, long[]> track2SampleSizes = new HashMap();
    private ByteBuffer sizeBuffer = null;

    public MP4Builder() {
    }

    public static long gcd(long a, long b) {
        return b == 0L ? a : gcd(b, a % b);
    }

    public MP4Builder createMovie(Mp4Movie mp4Movie) throws Exception {
        this.currentMp4Movie = mp4Movie;
        this.fos = new FileOutputStream(mp4Movie.getCacheFile());
        this.fc = this.fos.getChannel();
        FileTypeBox fileTypeBox = this.createFileTypeBox();
        fileTypeBox.getBox(this.fc);
        this.dataOffset += fileTypeBox.getSize();
        this.writedSinceLastMdat += this.dataOffset;
        this.mdat = new MP4Builder.InterleaveChunkMdat();
        this.sizeBuffer = ByteBuffer.allocateDirect(4);
        return this;
    }

    private void flushCurrentMdat() throws Exception {
        long oldPosition = this.fc.position();
        this.fc.position(this.mdat.getOffset());
        this.mdat.getBox(this.fc);
        this.fc.position(oldPosition);
        this.mdat.setDataOffset(0L);
        this.mdat.setContentSize(0L);
        this.fos.flush();
    }

    public boolean writeSampleData(int trackIndex, ByteBuffer byteBuf, BufferInfo bufferInfo, boolean isAudio) throws Exception {
        if (this.writeNewMdat) {
            this.mdat.setContentSize(0L);
            this.mdat.getBox(this.fc);
            this.mdat.setDataOffset(this.dataOffset);
            this.dataOffset += 16L;
            this.writedSinceLastMdat += 16L;
            this.writeNewMdat = false;
        }

        this.mdat.setContentSize(this.mdat.getContentSize() + (long) bufferInfo.size);
        this.writedSinceLastMdat += (long) bufferInfo.size;
        boolean flush = false;
        if (this.writedSinceLastMdat >= 32768L) {
            this.flushCurrentMdat();
            this.writeNewMdat = true;
            flush = true;
            this.writedSinceLastMdat -= 32768L;
        }

        this.currentMp4Movie.addSample(trackIndex, this.dataOffset, bufferInfo);
        byteBuf.position(bufferInfo.offset + (isAudio ? 0 : 4));
        byteBuf.limit(bufferInfo.offset + bufferInfo.size);
        if (!isAudio) {
            this.sizeBuffer.position(0);
            this.sizeBuffer.putInt(bufferInfo.size - 4);
            this.sizeBuffer.position(0);
            this.fc.write(this.sizeBuffer);
        }

        this.fc.write(byteBuf);
        this.dataOffset += (long) bufferInfo.size;
        if (flush) {
            this.fos.flush();
        }

        return flush;
    }

    public int addTrack(MediaFormat mediaFormat, boolean isAudio) throws Exception {
        return this.currentMp4Movie.addTrack(mediaFormat, isAudio);
    }

    public void finishMovie(boolean error) throws Exception {
        if (this.mdat.getContentSize() != 0L) {
            this.flushCurrentMdat();
        }

        Iterator var2 = this.currentMp4Movie.getTracks().iterator();

        while (var2.hasNext()) {
            Track track = (Track) var2.next();
            List<Sample> samples = track.getSamples();
            long[] sizes = new long[samples.size()];

            for (int i = 0; i < sizes.length; ++i) {
                sizes[i] = ((Sample) samples.get(i)).getSize();
            }

            this.track2SampleSizes.put(track, sizes);
        }

        Box moov = this.createMovieBox(this.currentMp4Movie);
        moov.getBox(this.fc);
        this.fos.flush();
        this.fc.close();
        this.fos.close();
    }

    protected FileTypeBox createFileTypeBox() {
        LinkedList<String> minorBrands = new LinkedList();
        minorBrands.add("isom");
        minorBrands.add("3gp4");
        return new FileTypeBox("isom", 0L, minorBrands);
    }

    public long getTimescale(Mp4Movie mp4Movie) {
        long timescale = 0L;
        if (!mp4Movie.getTracks().isEmpty()) {
            timescale = (long) ((Track) mp4Movie.getTracks().iterator().next()).getTimeScale();
        }

        Track track;
        for (Iterator var4 = mp4Movie.getTracks().iterator(); var4.hasNext(); timescale = gcd((long) track.getTimeScale(), timescale)) {
            track = (Track) var4.next();
        }

        return timescale;
    }

    protected MovieBox createMovieBox(Mp4Movie movie) {
        MovieBox movieBox = new MovieBox();
        MovieHeaderBox mvhd = new MovieHeaderBox();
        mvhd.setCreationTime(new Date());
        mvhd.setModificationTime(new Date());
        mvhd.setMatrix(Matrix.ROTATE_0);
        long movieTimeScale = this.getTimescale(movie);
        long duration = 0L;
        Iterator var8 = movie.getTracks().iterator();

        Track track;
        while (var8.hasNext()) {
            track = (Track) var8.next();
            long tracksDuration = track.getDuration() * movieTimeScale / (long) track.getTimeScale();
            if (tracksDuration > duration) {
                duration = tracksDuration;
            }
        }

        mvhd.setDuration(duration);
        mvhd.setTimescale(movieTimeScale);
        mvhd.setNextTrackId((long) (movie.getTracks().size() + 1));
        movieBox.addBox(mvhd);
        var8 = movie.getTracks().iterator();

        while (var8.hasNext()) {
            track = (Track) var8.next();
            movieBox.addBox(this.createTrackBox(track, movie));
        }

        return movieBox;
    }

    protected TrackBox createTrackBox(Track track, Mp4Movie movie) {
        TrackBox trackBox = new TrackBox();
        TrackHeaderBox tkhd = new TrackHeaderBox();
        tkhd.setEnabled(true);
        tkhd.setInMovie(true);
        tkhd.setInPreview(true);
        if (track.isAudio()) {
            tkhd.setMatrix(Matrix.ROTATE_0);
        } else {
            tkhd.setMatrix(movie.getMatrix());
        }

        tkhd.setAlternateGroup(0);
        tkhd.setCreationTime(track.getCreationTime());
        tkhd.setDuration(track.getDuration() * this.getTimescale(movie) / (long) track.getTimeScale());
        tkhd.setHeight((double) track.getHeight());
        tkhd.setWidth((double) track.getWidth());
        tkhd.setLayer(0);
        tkhd.setModificationTime(new Date());
        tkhd.setTrackId(track.getTrackId() + 1L);
        tkhd.setVolume(track.getVolume());
        trackBox.addBox(tkhd);
        MediaBox mdia = new MediaBox();
        trackBox.addBox(mdia);
        MediaHeaderBox mdhd = new MediaHeaderBox();
        mdhd.setCreationTime(track.getCreationTime());
        mdhd.setDuration(track.getDuration());
        mdhd.setTimescale((long) track.getTimeScale());
        mdhd.setLanguage("eng");
        mdia.addBox(mdhd);
        HandlerBox hdlr = new HandlerBox();
        hdlr.setName(track.isAudio() ? "SoundHandle" : "VideoHandle");
        hdlr.setHandlerType(track.getHandler());
        mdia.addBox(hdlr);
        MediaInformationBox minf = new MediaInformationBox();
        minf.addBox(track.getMediaHeaderBox());
        DataInformationBox dinf = new DataInformationBox();
        DataReferenceBox dref = new DataReferenceBox();
        dinf.addBox(dref);
        DataEntryUrlBox url = new DataEntryUrlBox();
        url.setFlags(1);
        dref.addBox(url);
        minf.addBox(dinf);
        Box stbl = this.createStbl(track);
        minf.addBox(stbl);
        mdia.addBox(minf);
        return trackBox;
    }

    protected Box createStbl(Track track) {
        SampleTableBox stbl = new SampleTableBox();
        this.createStsd(track, stbl);
        this.createStts(track, stbl);
        this.createStss(track, stbl);
        this.createStsc(track, stbl);
        this.createStsz(track, stbl);
        this.createStco(track, stbl);
        return stbl;
    }

    protected void createStsd(Track track, SampleTableBox stbl) {
        stbl.addBox(track.getSampleDescriptionBox());
    }

    protected void createStts(Track track, SampleTableBox stbl) {
        Entry lastEntry = null;
        List<Entry> entries = new ArrayList();
        Iterator var5 = track.getSampleDurations().iterator();

        while (true) {
            while (var5.hasNext()) {
                long delta = (Long) var5.next();
                if (lastEntry != null && lastEntry.getDelta() == delta) {
                    lastEntry.setCount(lastEntry.getCount() + 1L);
                } else {
                    lastEntry = new Entry(1L, delta);
                    entries.add(lastEntry);
                }
            }

            TimeToSampleBox stts = new TimeToSampleBox();
            stts.setEntries(entries);
            stbl.addBox(stts);
            return;
        }
    }

    protected void createStss(Track track, SampleTableBox stbl) {
        long[] syncSamples = track.getSyncSamples();
        if (syncSamples != null && syncSamples.length > 0) {
            SyncSampleBox stss = new SyncSampleBox();
            stss.setSampleNumber(syncSamples);
            stbl.addBox(stss);
        }

    }

    protected void createStsc(Track track, SampleTableBox stbl) {
        SampleToChunkBox stsc = new SampleToChunkBox();
        stsc.setEntries(new LinkedList());
        long lastOffset = -1L;
        int lastChunkNumber = 1;
        int lastSampleCount = 0;
        int previousWritedChunkCount = -1;
        int samplesCount = track.getSamples().size();

        for (int a = 0; a < samplesCount; ++a) {
            Sample sample = (Sample) track.getSamples().get(a);
            long offset = sample.getOffset();
            long size = sample.getSize();
            lastOffset = offset + size;
            ++lastSampleCount;
            boolean write = false;
            if (a != samplesCount - 1) {
                Sample nextSample = (Sample) track.getSamples().get(a + 1);
                if (lastOffset != nextSample.getOffset()) {
                    write = true;
                }
            } else {
                write = true;
            }

            if (write) {
                if (previousWritedChunkCount != lastSampleCount) {
                    stsc.getEntries().add(new com.coremedia.iso.boxes.SampleToChunkBox.Entry((long) lastChunkNumber, (long) lastSampleCount, 1L));
                    previousWritedChunkCount = lastSampleCount;
                }

                lastSampleCount = 0;
                ++lastChunkNumber;
            }
        }

        stbl.addBox(stsc);
    }

    protected void createStsz(Track track, SampleTableBox stbl) {
        SampleSizeBox stsz = new SampleSizeBox();
        stsz.setSampleSizes((long[]) this.track2SampleSizes.get(track));
        stbl.addBox(stsz);
    }

    protected void createStco(Track track, SampleTableBox stbl) {
        ArrayList<Long> chunksOffsets = new ArrayList();
        long lastOffset = -1L;

        Sample sample;
        long offset;
        for (Iterator var6 = track.getSamples().iterator(); var6.hasNext(); lastOffset = offset + sample.getSize()) {
            sample = (Sample) var6.next();
            offset = sample.getOffset();
            if (lastOffset != -1L && lastOffset != offset) {
                lastOffset = -1L;
            }

            if (lastOffset == -1L) {
                chunksOffsets.add(offset);
            }
        }

        long[] chunkOffsetsLong = new long[chunksOffsets.size()];

        for (int a = 0; a < chunksOffsets.size(); ++a) {
            chunkOffsetsLong[a] = (Long) chunksOffsets.get(a);
        }

        StaticChunkOffsetBox stco = new StaticChunkOffsetBox();
        stco.setChunkOffsets(chunkOffsetsLong);
        stbl.addBox(stco);
    }

    private class InterleaveChunkMdat implements Box {
        private Container parent;
        private long contentSize;
        private long dataOffset;

        private InterleaveChunkMdat() {
            this.contentSize = 1073741824L;
            this.dataOffset = 0L;
        }

        public Container getParent() {
            return this.parent;
        }

        public void setParent(Container parent) {
            this.parent = parent;
        }

        public long getOffset() {
            return this.dataOffset;
        }

        public void setDataOffset(long offset) {
            this.dataOffset = offset;
        }

        public long getContentSize() {
            return this.contentSize;
        }

        public void setContentSize(long contentSize) {
            this.contentSize = contentSize;
        }

        public String getType() {
            return "mdat";
        }

        public long getSize() {
            return 16L + this.contentSize;
        }

        private boolean isSmallBox(long contentSize) {
            return contentSize + 8L < 4294967296L;
        }

        public void parse(DataSource dataSource, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        }

        public void getBox(WritableByteChannel writableByteChannel) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(16);
            long size = this.getSize();
            if (this.isSmallBox(size)) {
                IsoTypeWriter.writeUInt32(bb, size);
            } else {
                IsoTypeWriter.writeUInt32(bb, 1L);
            }

            bb.put(IsoFile.fourCCtoBytes("mdat"));
            if (this.isSmallBox(size)) {
                bb.put(new byte[8]);
            } else {
                IsoTypeWriter.writeUInt64(bb, size);
            }

            bb.rewind();
            writableByteChannel.write(bb);
        }
    }
}